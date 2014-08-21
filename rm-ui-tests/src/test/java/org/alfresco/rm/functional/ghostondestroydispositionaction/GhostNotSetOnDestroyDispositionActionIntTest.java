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
package org.alfresco.rm.functional.ghostondestroydispositionaction;

import org.alfresco.po.share.util.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test tests the setting of the ghost on destroy disposition action
 * 
 * @author Mark Hibbins
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class GhostNotSetOnDestroyDispositionActionIntTest extends GhostOnDestroyDispositionActionIntTestBase
{
    @Test
    public void testNoGhostDestroyDispositionAction()
    {
        testGhostDestroyDispositionAction(false);
    }
}