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

import java.util.List;

import org.alfresco.bm.dataload.rm.role.RMRole;
import org.alfresco.bm.site.SiteDataService;
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
public class PrepareRMSiteMembersUnitTest
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
        prepareRMSiteMembers.setRole("ADMINISTRATOR, RECORDS_MANAGER, SECURITY_OFFICER, POWER_USER, USER");
        List<RMRole> rolesToChoseFrom = prepareRMSiteMembers.getRolesToChoseFrom();
        assertEquals(5, rolesToChoseFrom.size());
        assertTrue(rolesToChoseFrom.contains(RMRole.ADMINISTRATOR));
        assertTrue(rolesToChoseFrom.contains(RMRole.RECORDS_MANAGER));
        assertTrue(rolesToChoseFrom.contains(RMRole.SECURITY_OFFICER));
        assertTrue(rolesToChoseFrom.contains(RMRole.POWER_USER));
        assertTrue(rolesToChoseFrom.contains(RMRole.USER));

        prepareRMSiteMembers.setRole("ADMINISTRATOR, RECORDS_MANAGER");
        rolesToChoseFrom = prepareRMSiteMembers.getRolesToChoseFrom();
        assertEquals(2, rolesToChoseFrom.size());
        assertTrue(rolesToChoseFrom.contains(RMRole.ADMINISTRATOR));
        assertTrue(rolesToChoseFrom.contains(RMRole.RECORDS_MANAGER));

        prepareRMSiteMembers.setRole("ADMINISTRATOR");
        rolesToChoseFrom = prepareRMSiteMembers.getRolesToChoseFrom();
        assertEquals(1, rolesToChoseFrom.size());
        assertTrue(rolesToChoseFrom.contains(RMRole.ADMINISTRATOR));
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
    public void testInexistentRoleBetweenExistentRoles() throws Exception
    {
        prepareRMSiteMembers.setRole("ADMINISTRATOR, RECORDS_MANAGER, SECURITY_OFFICER,inexistent1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInexistentRole() throws Exception
    {
        prepareRMSiteMembers.setRole("inexistent1");
    }
}
