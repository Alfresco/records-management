/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.bm.dataload.rm.role;

import java.util.Random;

import org.alfresco.rest.rm.community.model.user.UserRoles;

/**
 * Site membership role based on record management site.
 *
 * @author Michael Suzuki
 * @author Silviu Dinuta
 * @since 2.6
 */
public enum RMRole
{
    USER(UserRoles.ROLE_RM_USER),
    POWER_USER(UserRoles.ROLE_RM_POWER_USER),
    SECURITY_OFFICER(UserRoles.ROLE_RM_SECURITY_OFFICER),
    RECORDS_MANAGER(UserRoles.ROLE_RM_MANAGER),
    ADMINISTRATOR(UserRoles.ROLE_RM_ADMIN);

    private String text;
    private static Random random = new Random();

    private RMRole(String text)
    {
        this.text = text;
    }
    public static RMRole getRandomRole()
    {
        return values()[random.nextInt(values().length)];
    }

    @Override
    public String toString()
    {
        return text;
    }
}
