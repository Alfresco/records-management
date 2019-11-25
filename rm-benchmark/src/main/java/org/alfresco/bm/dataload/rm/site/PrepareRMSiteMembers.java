/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 * #L%
 */

package org.alfresco.bm.dataload.rm.site;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.dataload.rm.role.RMRole;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.bm.user.UserData;

/**
 * Prepares RM site members for creation by populating the site members collection.
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
public class PrepareRMSiteMembers extends RMBaseEventProcessor
{
    public static final String NO_NEW_USERS_FOUND_MSG = "No new users found to assign to RM, continue loading data.";
    public static final String NO_USERS_AVAILABLE_MSG = "There are no users available, continue loading data.";
    public static final String NO_USERS_WANTED_MSG = "No users wanted, continue loading data.";
    public static final String ASSIGNATION_NOT_WANTED_MSG = "Assignation of RM users not wanted, continue loading data.";
    public static final String EVENT_NAME_SITE_MEMBERS_PREPARED = "rmSiteMembersPrepared";
    public static final String EVENT_NAME_CONTINUE_LOADING_DATA = "scheduleFilePlanLoaders";
    public static final String PREPARED_MSG_TEMPLATE = "Prepared {0} site members";
    public static final String PREPARED_INCOMPLETE_MSG_TEMPLATE = "Prepared only {0} site members, and the requested number of users was {1}.";
    private boolean assignRMRoleToUsers;
    private int userCount;
    private List<RMRole> rolesToChoseFrom = new ArrayList<RMRole>();
    private String eventNameSiteMembersPrepared = EVENT_NAME_SITE_MEMBERS_PREPARED;
    private String eventNameContinueLoadingData = EVENT_NAME_CONTINUE_LOADING_DATA;

    public boolean isAssignRMRoleToUsers()
    {
        return assignRMRoleToUsers;
    }

    public void setAssignRMRoleToUsers(boolean assignRMRoleToUsers)
    {
        this.assignRMRoleToUsers = assignRMRoleToUsers;
    }

    public String getEventNameSiteMembersPrepared()
    {
        return eventNameSiteMembersPrepared;
    }

    public void setEventNameSiteMembersPrepared(String eventNameSiteMembersPrepared)
    {
        this.eventNameSiteMembersPrepared = eventNameSiteMembersPrepared;
    }

    public String getEventNameContinueLoadingData()
    {
        return eventNameContinueLoadingData;
    }

    public void setEventNameContinueLoadingData(String eventNameContinueLoadingData)
    {
        this.eventNameContinueLoadingData = eventNameContinueLoadingData;
    }

    public int getUserCount()
    {
        return userCount;
    }

    public void setUserCount(int userCount)
    {
        this.userCount = userCount;
    }

    public List<RMRole> getRolesToChoseFrom()
    {
        return rolesToChoseFrom;
    }

    public void setRole(String role)
    {
        rolesToChoseFrom = new ArrayList<RMRole>();
        if (isBlank(role))
        {
            throw new IllegalArgumentException("'role' may not be null or empty.");
        }
        // Split by comma
        String[] roles = split(role, ",");
        if(roles.length < 1)
        {
            throw new IllegalArgumentException("'role' has to contain at least one RM Role, ',,,' values are not allowed.");
        }
        for (String roleValue : roles)
        {
            String roleStr = roleValue.trim();
            try
            {
                RMRole chosenRole = RMRole.valueOf(roleStr);
                if(!rolesToChoseFrom.contains(chosenRole))
                {
                    rolesToChoseFrom.add(chosenRole);
                }
            }
            catch(Exception ex)
            {
                throw new IllegalArgumentException(roleStr + " does not have one of the allowed values.");
            }
        }
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        if (!isAssignRMRoleToUsers())
        {
            return new EventResult(ASSIGNATION_NOT_WANTED_MSG, new Event(getEventNameContinueLoadingData(), null));
        }
        if(getUserCount() <= 0)
        {
            return new EventResult(NO_USERS_WANTED_MSG, new Event(getEventNameContinueLoadingData(), null));
        }

        int membersCount = 0;
        int userSkip = 0;
        final int userPageSize = 100;
        List<UserData> users = userDataService.getUsersByCreationState(DataCreationState.Created, userSkip, userPageSize);
        if (users.isEmpty())
        {
            return new EventResult(NO_USERS_AVAILABLE_MSG, new Event(getEventNameContinueLoadingData(), null));
        }

        String siteId = PATH_SNIPPET_RM_SITE_ID;
        // How many users do we have for RM site?
        int currentSiteUsersCount = siteDataService.getSiteMembers(siteId, DataCreationState.Created, null, 0, getUserCount()).size();
        int siteUsersToCreate = getUserCount() - currentSiteUsersCount;

        // Keep going while we attempt to find a user to use
        while(!users.isEmpty() && membersCount < siteUsersToCreate)
        {
            for(UserData user : users)
            {
                if(membersCount == siteUsersToCreate)
                {
                    break;
                }
                // Check if the user is already a member
                String username = user.getUsername();
                SiteMemberData siteMember = siteDataService.getSiteMember(siteId, username);
                if (siteMember != null)
                {
                    // The user is already a set to be a site member (we could hit site manager)
                    continue;
                }
                // Create the membership
                siteMember = new SiteMemberData();
                siteMember.setCreationState(DataCreationState.NotScheduled);
                RMRole randomRole = getRandomRole();
                siteMember.setRole(randomRole.toString());
                siteMember.setSiteId(siteId);
                siteMember.setUsername(username);
                siteDataService.addSiteMember(siteMember);
                membersCount++;
            }
            userSkip += users.size();
            users = userDataService.getUsersByCreationState(DataCreationState.Created, userSkip, userPageSize);
        }

        if(membersCount == 0)
        {
            return new EventResult(NO_NEW_USERS_FOUND_MSG, new Event(getEventNameContinueLoadingData(), null));
        }

        String msg = MessageFormat.format(PREPARED_MSG_TEMPLATE, membersCount);
        if(membersCount < siteUsersToCreate)
        {
            msg = MessageFormat.format(PREPARED_INCOMPLETE_MSG_TEMPLATE, membersCount, siteUsersToCreate);
        }

        // We need an event to mark completion
        Event outputEvent = new Event(eventNameSiteMembersPrepared, null);

        // Create result
        EventResult result = new EventResult(msg, Collections.singletonList(outputEvent));

        // Done
        logger.debug(msg);
        return result;
    }

    /**
     * Helper method to obtain one RM role randomly from preconfigured list available roles
     *
     * @return RMRole enum value obtained from the preconfigured list available roles
     */
    private RMRole getRandomRole()
    {
        Random random = new Random();
        return rolesToChoseFrom.get(random.nextInt(rolesToChoseFrom.size()));
    }

}
