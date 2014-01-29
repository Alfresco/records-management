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
package org.alfresco.po.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Util class for the RM PO project
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public final class RmUtils
{
    /**
     * Helper method to check the parameters. This method should be used for all public methods.
     *
     * @param paramName {@link String} A name for the parameter to check
     * @param object {@link Object} The object to check
     * @exception IllegalArgumentException will be thrown if the parameter value is null
     * (for {@link String} also if the value is empty or blank)
     */
    public static final void checkMandotaryParam(String paramName, Object object)
    {
        if (object == null || (object instanceof String && StringUtils.isBlank((String) object)))
        {
            throw new IllegalArgumentException("'" + paramName + "' is a mandatory parameter!");
        }
    }
}