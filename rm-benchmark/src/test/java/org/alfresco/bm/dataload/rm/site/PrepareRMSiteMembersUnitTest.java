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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.dataload.rm.role.RMRole;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.bm.user.UserData;
import org.alfresco.bm.user.UserDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test class for PrepareRMSiteMembers
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class PrepareRMSiteMembersUnitTest implements RMEventConstants
{
    @Mock
    private UserDataService mockedUserDataService;

    @Mock
    private SiteDataService mockedSiteDataService;

    @InjectMocks
    private PrepareRMSiteMembers prepareRMSiteMembers;

    @Test
    public void testExistentRolesValues() throws Exception
    {
        prepareRMSiteMembers.setRole("Administrator, RecordsManager, SecurityOfficer, PowerUser, User");
        List<RMRole> rolesToChoseFrom = prepareRMSiteMembers.getRolesToChoseFrom();
        assertEquals(5, rolesToChoseFrom.size());
        assertTrue(rolesToChoseFrom.contains(RMRole.Administrator));
        assertTrue(rolesToChoseFrom.contains(RMRole.RecordsManager));
        assertTrue(rolesToChoseFrom.contains(RMRole.SecurityOfficer));
        assertTrue(rolesToChoseFrom.contains(RMRole.PowerUser));
        assertTrue(rolesToChoseFrom.contains(RMRole.User));

        prepareRMSiteMembers.setRole("Administrator, RecordsManager");
        rolesToChoseFrom = prepareRMSiteMembers.getRolesToChoseFrom();
        assertEquals(2, rolesToChoseFrom.size());
        assertTrue(rolesToChoseFrom.contains(RMRole.Administrator));
        assertTrue(rolesToChoseFrom.contains(RMRole.RecordsManager));

        prepareRMSiteMembers.setRole("Administrator");
        rolesToChoseFrom = prepareRMSiteMembers.getRolesToChoseFrom();
        assertEquals(1, rolesToChoseFrom.size());
        assertTrue(rolesToChoseFrom.contains(RMRole.Administrator));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAvailableRolesConfigured() throws Exception
    {
        prepareRMSiteMembers.setRole(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyAvailableRolesConfigured() throws Exception
    {
        prepareRMSiteMembers.setRole("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyStringRolesConfigured() throws Exception
    {
        prepareRMSiteMembers.setRole(",,,");
    }

    @Test
    public void testOneRoleAndEmptyStringRolesConfigured() throws Exception
    {
        prepareRMSiteMembers.setRole(",Administrator,,");
        List<RMRole> rolesToChoseFrom = prepareRMSiteMembers.getRolesToChoseFrom();
        assertEquals(1, rolesToChoseFrom.size());
        assertTrue(rolesToChoseFrom.contains(RMRole.Administrator));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInexistentRoleBetweenExistentRoles() throws Exception
    {
        prepareRMSiteMembers.setRole("Administrator, RecordsManager, SecurityOfficer,inexistent1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInexistentRole() throws Exception
    {
        prepareRMSiteMembers.setRole("inexistent1");
    }

    @Test
    public void testAssignRMRolesNotWanted() throws Exception
    {
        prepareRMSiteMembers.setRole("Administrator");
        prepareRMSiteMembers.setAssignRMRoleToUsers(false);
        EventResult result = prepareRMSiteMembers.processEvent(null);
        assertEquals(true, result.isSuccess());
        assertEquals(PrepareRMSiteMembers.ASSIGNATION_NOT_WANTED_MSG, result.getData());
        List<Event> nextEvents = result.getNextEvents();
        assertEquals(1, nextEvents.size());
        assertEquals(prepareRMSiteMembers.getEventNameContinueLoadingData(), nextEvents.get(0).getName());
    }

    @Test
    public void testNoUsersWanted() throws Exception
    {
        prepareRMSiteMembers.setRole("Administrator");
        prepareRMSiteMembers.setAssignRMRoleToUsers(true);
        prepareRMSiteMembers.setUserCount(0);
        EventResult result = prepareRMSiteMembers.processEvent(null);
        assertEquals(true, result.isSuccess());
        assertEquals(PrepareRMSiteMembers.NO_USERS_WANTED_MSG, result.getData());
        List<Event> nextEvents = result.getNextEvents();
        assertEquals(1, nextEvents.size());
        assertEquals(prepareRMSiteMembers.getEventNameContinueLoadingData(), nextEvents.get(0).getName());
    }

    @Test
    public void testNoUsersToChoseFrom() throws Exception
    {
        prepareRMSiteMembers.setRole("Administrator");
        prepareRMSiteMembers.setAssignRMRoleToUsers(true);
        prepareRMSiteMembers.setUserCount(1);
        when(mockedUserDataService.getUsersByCreationState(DataCreationState.Created, 0, 100)).thenReturn(new ArrayList<>());
        EventResult result = prepareRMSiteMembers.processEvent(null);
        assertEquals(true, result.isSuccess());
        assertEquals(PrepareRMSiteMembers.NO_USERS_AVAILABLE_MSG, result.getData());
        List<Event> nextEvents = result.getNextEvents();
        assertEquals(1, nextEvents.size());
        assertEquals(prepareRMSiteMembers.getEventNameContinueLoadingData(), nextEvents.get(0).getName());
    }

    @Test
    public void testNoNewUsersAvailable() throws Exception
    {
        int userCount = 2;
        String user1 = "user1";
        String user2 = "user2";

        prepareRMSiteMembers.setRole("Administrator");
        prepareRMSiteMembers.setAssignRMRoleToUsers(true);
        prepareRMSiteMembers.setUserCount(userCount);
        UserData mockedUserData1 = mock(UserData.class);
        when(mockedUserData1.getUsername()).thenReturn(user1);
        UserData mockedUserData2 = mock(UserData.class);
        when(mockedUserData2.getUsername()).thenReturn(user2);
        List<UserData> users = Arrays.asList(mockedUserData1, mockedUserData2);
        when(mockedUserDataService.getUsersByCreationState(DataCreationState.Created, 0, 100)).thenReturn(users);
        when(mockedSiteDataService.getSiteMembers(PATH_SNIPPET_RM_SITE_ID, DataCreationState.Created, null, 0, userCount)).thenReturn(new ArrayList<>());

        SiteMemberData mockedSiteMemberData1 = mock(SiteMemberData.class);
        SiteMemberData mockedSiteMemberData2 = mock(SiteMemberData.class);
        when(mockedSiteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, user1)).thenReturn(mockedSiteMemberData1);
        when(mockedSiteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, user2)).thenReturn(mockedSiteMemberData2);

        EventResult result = prepareRMSiteMembers.processEvent(null);
        verify(mockedSiteDataService, never()).addSiteMember(any(SiteMemberData.class));
        assertEquals(true, result.isSuccess());
        assertEquals(PrepareRMSiteMembers.NO_NEW_USERS_FOUND_MSG, result.getData());
        List<Event> nextEvents = result.getNextEvents();
        assertEquals(1, nextEvents.size());
        assertEquals(prepareRMSiteMembers.getEventNameContinueLoadingData(), nextEvents.get(0).getName());
    }

    @Test
    public void testLessThanRequestedNumeberOfUsersFound() throws Exception
    {
        int userCount = 2;
        String user1 = "user1";
        String user2 = "user2";

        prepareRMSiteMembers.setRole("Administrator");
        prepareRMSiteMembers.setAssignRMRoleToUsers(true);
        prepareRMSiteMembers.setUserCount(userCount);
        UserData mockedUserData1 = mock(UserData.class);
        when(mockedUserData1.getUsername()).thenReturn(user1);
        UserData mockedUserData2 = mock(UserData.class);
        when(mockedUserData2.getUsername()).thenReturn(user2);
        List<UserData> users = Arrays.asList(mockedUserData1, mockedUserData2);
        when(mockedUserDataService.getUsersByCreationState(DataCreationState.Created, 0, 100)).thenReturn(users);
        when(mockedSiteDataService.getSiteMembers(PATH_SNIPPET_RM_SITE_ID, DataCreationState.Created, null, 0, userCount)).thenReturn(new ArrayList<>());

        SiteMemberData mockedSiteMemberData1 = mock(SiteMemberData.class);
        when(mockedSiteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, user1)).thenReturn(mockedSiteMemberData1);
        when(mockedSiteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, user2)).thenReturn(null);

        EventResult result = prepareRMSiteMembers.processEvent(null);
        verify(mockedSiteDataService, times(1)).addSiteMember(any(SiteMemberData.class));
        assertEquals(true, result.isSuccess());
        assertEquals(MessageFormat.format(PrepareRMSiteMembers.PREPARED_INCOMPLETE_MSG_TEMPLATE, 1, userCount), result.getData());
        List<Event> nextEvents = result.getNextEvents();
        assertEquals(1, nextEvents.size());
        assertEquals(prepareRMSiteMembers.getEventNameSiteMembersPrepared(), nextEvents.get(0).getName());
    }

    @Test
    public void testAllRequestedNumeberOfUsersFound() throws Exception
    {
        int userCount = 2;
        String user1 = "user1";
        String user2 = "user2";

        prepareRMSiteMembers.setRole("Administrator");
        prepareRMSiteMembers.setAssignRMRoleToUsers(true);
        prepareRMSiteMembers.setUserCount(userCount);
        UserData mockedUserData1 = mock(UserData.class);
        when(mockedUserData1.getUsername()).thenReturn(user1);
        UserData mockedUserData2 = mock(UserData.class);
        when(mockedUserData2.getUsername()).thenReturn(user2);
        List<UserData> users = Arrays.asList(mockedUserData1, mockedUserData2);
        when(mockedUserDataService.getUsersByCreationState(DataCreationState.Created, 0, 100)).thenReturn(users);
        when(mockedSiteDataService.getSiteMembers(PATH_SNIPPET_RM_SITE_ID, DataCreationState.Created, null, 0, userCount)).thenReturn(new ArrayList<>());

        when(mockedSiteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, user1)).thenReturn(null);
        when(mockedSiteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, user2)).thenReturn(null);

        EventResult result = prepareRMSiteMembers.processEvent(null);
        verify(mockedSiteDataService, times(2)).addSiteMember(any(SiteMemberData.class));
        assertEquals(true, result.isSuccess());
        assertEquals(MessageFormat.format(PrepareRMSiteMembers.PREPARED_MSG_TEMPLATE, userCount), result.getData());
        List<Event> nextEvents = result.getNextEvents();
        assertEquals(1, nextEvents.size());
        assertEquals(prepareRMSiteMembers.getEventNameSiteMembersPrepared(), nextEvents.get(0).getName());
    }
}
