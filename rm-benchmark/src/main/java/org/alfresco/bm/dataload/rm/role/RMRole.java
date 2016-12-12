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

/**
 * Site membership role based on record management site.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public enum RMRole
{
    USER,
    POWER_USER,
    SECURITY_OFFICER,
    RECORDS_MANAGER,
    ADMINISTRATOR;

    private static Random random = new Random();

    public static RMRole getRandomRole()
    {
        return values()[random.nextInt(values().length)];
    }
}
