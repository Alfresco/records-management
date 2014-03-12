/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.rm.functional;

import java.io.IOException;
import java.util.List;

import org.alfresco.po.rm.RmCopyOrMoveUnfiledContentPage;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.unfiledrecords.UnfiledRecordsContainer;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordFolderDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test suite tests the following new features:
 * <p>
 * <ul>
 *  <li> TODO - complete lists of tests
 *  <li>Filing a record into the unfiled records container (directly to the root or into folders in the container)</li>
 *  <li>Changing the meta data of a folder in the unfiled records container</li>
 *  <li>Viewing the details of a folder in the unfiled records container</li>
 *  <li>Viewing the manage permissions page of the unfiled records container</li>
 *  <li>Viewing the manage rules page of the unfiled records container</li>
 *  <li>Deleting a record/folder in the unfiled records container</li>
 * </ul>
 * <p>
 * @author Mark Hibbins
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class UnfiledRecordsCopyAndMoveIntTest extends AbstractIntegrationTest
{
    private static final By NAVIGATION_MENU_FILE_PLAN = By.cssSelector("div#HEADER_SITE_DOCUMENTLIBRARY");
    private UnfiledRecordsContainer unfiledRecordsContainer;

    /**
     * Navigate to the unfiled records container
     */
    private void navigateToUnfiledRecords()
    {
        RmPageObjectUtils.select(drone, NAVIGATION_MENU_FILE_PLAN);
        FilePlanPage filePlanPage = drone.getCurrentPage().render();
        Assert.assertNotNull(filePlanPage);
        FilePlanFilter filePlanFilter = filePlanPage.getFilePlanFilter();
        Assert.assertNotNull(filePlanFilter);
        Assert.assertTrue(filePlanFilter.isUnfiledRecordsContainerDisplayed());
        unfiledRecordsContainer = filePlanFilter.selectUnfiledRecordsContainer().render();
        Assert.assertNotNull(unfiledRecordsContainer);
    }

    /**
     * Create an unfiled folder in the current folder
     *
     * @param name  Name of folder to create
     */
    private void createUnfiledFolder(String name)
    {
        CreateNewRecordFolderDialog createNewFolderDialog = unfiledRecordsContainer.selectCreateNewUnfiledRecordsContainerFolder().render();
        Assert.assertNotNull(createNewFolderDialog);
        Assert.assertNotNull(createNewFolderDialog.getRecordFolderId());
        createNewFolderDialog.enterName(name);
        createNewFolderDialog.enterTitle(name);
        createNewFolderDialog.selectSave();
    }

    /**
     * Navigate to specified path
     *
     * @param path  Path in the form /folder/folder/folder
     */
    private void navigateToPath(String path)
    {
        navigateToUnfiledRecords();
        String pathElements[] = path.split("/");
        for(String pathElement : pathElements)
        {
            if((pathElement != null) && !"".equals(pathElement))
            {
                unfiledRecordsContainer = unfiledRecordsContainer.selectUnfiledFolder(pathElement, 10000).render();
            }
        }
    }

    /**
     * Test that copies a record within the same folder
     *
     * @throws IOException
     */
    @Test(enabled = true)
    public void copyUnfiledRecordLocally() throws IOException
    {
        // create test folder
        navigateToUnfiledRecords();
        createUnfiledFolder("test-1");
        navigateToPath("/test-1");

        // create test source record
        RmUploadFilePage rmRecordFileDialog = unfiledRecordsContainer.selectCreateNewUnfiledRecordsContainerFile().render();
        Assert.assertNotNull(rmRecordFileDialog);
        fileElectronicRecordToUnfiledRecordsContainer(drone, rmRecordFileDialog, "sourceRecord");
        unfiledRecordsContainer = unfiledRecordsContainer.render("sourceRecord");

        // copy test source record
        List<FileDirectoryInfo> files = unfiledRecordsContainer.getFiles();
        Assert.assertEquals(files.size(), 1);
        FileDirectoryInfo sourceFile = files.get(0);
        sourceFile.selectCopyTo();
        RmCopyOrMoveUnfiledContentPage copyOrMoveUnfiledContentPage = new RmCopyOrMoveUnfiledContentPage(this.drone);
        copyOrMoveUnfiledContentPage.render();
        copyOrMoveUnfiledContentPage.selectPath("/test-1");

        // ensure that we now have 2 files in this directory
        navigateToUnfiledRecords();
        navigateToPath("/test-1");

        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 2);
    }

    /**
     * Test that copies a record to a different folder
     *
     * @throws IOException
     */
    @Test(enabled = true)
    public void copyUnfiledRecordToDifferentFolder() throws IOException
    {
        // create test folder
        navigateToUnfiledRecords();
        createUnfiledFolder("test-2");
        navigateToPath("/test-2");

        // create source and destination folders
        createUnfiledFolder("source");
        createUnfiledFolder("destination");
        navigateToPath("/test-2/source");

        // create test source record
        RmUploadFilePage rmRecordFileDialog = unfiledRecordsContainer.selectCreateNewUnfiledRecordsContainerFile().render();
        Assert.assertNotNull(rmRecordFileDialog);
        fileElectronicRecordToUnfiledRecordsContainer(drone, rmRecordFileDialog, "sourceRecord");
        unfiledRecordsContainer = unfiledRecordsContainer.render("sourceRecord");

        // copy test source record
        List<FileDirectoryInfo> files = unfiledRecordsContainer.getFiles();
        Assert.assertEquals(files.size(), 1);
        FileDirectoryInfo sourceFile = files.get(0);
        sourceFile.selectCopyTo();
        RmCopyOrMoveUnfiledContentPage copyOrMoveUnfiledContentPage = new RmCopyOrMoveUnfiledContentPage(this.drone);
        copyOrMoveUnfiledContentPage.render();
        copyOrMoveUnfiledContentPage.selectPath("/test-2/destination");

        // ensure that we now have 1 file in the source directory
        navigateToUnfiledRecords();
        navigateToPath("/test-2/source");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 1);

        // ensure that we now have 1 file in the destination directory
        navigateToUnfiledRecords();
        navigateToPath("/test-2/destination");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 1);
    }

    /**
     * Test that copies a folder containing a record to a different folder
     *
     * @throws IOException
     */
    @Test(enabled = true)
    public void copyUnfiledRecordFolderToDifferentFolder() throws IOException
    {
        // create test folder
        navigateToUnfiledRecords();
        createUnfiledFolder("test-3");
        navigateToPath("/test-3");

        // create source and destination folders
        createUnfiledFolder("destination");
        createUnfiledFolder("source");
        navigateToPath("/test-3/source");
        createUnfiledFolder("sourceFolder");

        // create test source record
        navigateToPath("/test-3/source/sourceFolder");
        RmUploadFilePage rmRecordFileDialog = unfiledRecordsContainer.selectCreateNewUnfiledRecordsContainerFile().render();
        Assert.assertNotNull(rmRecordFileDialog);
        fileElectronicRecordToUnfiledRecordsContainer(drone, rmRecordFileDialog, "sourceRecord");
        unfiledRecordsContainer = unfiledRecordsContainer.render("sourceRecord");

        // copy test source folder
        navigateToPath("/test-3/source");
        List<FileDirectoryInfo> files = unfiledRecordsContainer.getFiles();
        Assert.assertEquals(files.size(), 1);
        FileDirectoryInfo sourceFile = files.get(0);
        sourceFile.selectCopyTo();
        RmCopyOrMoveUnfiledContentPage copyOrMoveUnfiledContentPage = new RmCopyOrMoveUnfiledContentPage(this.drone);
        copyOrMoveUnfiledContentPage.render();
        copyOrMoveUnfiledContentPage.selectPath("/test-3/destination");

        // ensure that we now have still have the folder in the source directory
        navigateToUnfiledRecords();
        navigateToPath("/test-3/source");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 1);
        navigateToPath("/test-3/source/sourceFolder");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 1);

        // ensure that we now have the copied folder in the destination directory
        navigateToUnfiledRecords();
        navigateToPath("/test-3/destination");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 1);
        navigateToPath("/test-3/destination/sourceFolder");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 1);
    }

    /**
     * Move a record to a different folder
     *
     * @throws IOException
     */
    @Test(enabled = true)
    public void moveUnfiledRecordToDifferentFolder() throws IOException
    {
        // create test folder
        navigateToUnfiledRecords();
        createUnfiledFolder("test-4");
        navigateToPath("/test-4");

        // create source and destination folders
        createUnfiledFolder("source");
        createUnfiledFolder("destination");
        navigateToPath("/test-4/source");

        // create test source record
        RmUploadFilePage rmRecordFileDialog = unfiledRecordsContainer.selectCreateNewUnfiledRecordsContainerFile().render();
        Assert.assertNotNull(rmRecordFileDialog);
        fileElectronicRecordToUnfiledRecordsContainer(drone, rmRecordFileDialog, "sourceRecord");
        unfiledRecordsContainer = unfiledRecordsContainer.render("sourceRecord");

        // test source record
        List<FileDirectoryInfo> files = unfiledRecordsContainer.getFiles();
        Assert.assertEquals(files.size(), 1);
        FileDirectoryInfo sourceFile = files.get(0);
        sourceFile.selectMoveTo();
        RmCopyOrMoveUnfiledContentPage copyOrMoveUnfiledContentPage = new RmCopyOrMoveUnfiledContentPage(this.drone);
        copyOrMoveUnfiledContentPage.render();
        copyOrMoveUnfiledContentPage.selectPath("/test-4/destination");

        // ensure that we now have no files in the source directory
        navigateToUnfiledRecords();
        navigateToPath("/test-4/source");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 0);

        // ensure that we now have 1 files in the destination directory
        navigateToUnfiledRecords();
        navigateToPath("/test-4/destination");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 1);
    }

    /**
     * Test that copies a folder to a different folder
     *
     * @throws IOException
     */
    @Test(enabled = true)
    public void moveUnfiledRecordFolderToDifferentFolder() throws IOException
    {
        // create test folder
        navigateToUnfiledRecords();
        createUnfiledFolder("test-5");
        navigateToPath("/test-5");

        // create source and destination folders
        createUnfiledFolder("destination");
        createUnfiledFolder("source");
        navigateToPath("/test-5/source");
        createUnfiledFolder("sourceFolder");

        // create test source record
        navigateToPath("/test-5/source/sourceFolder");
        RmUploadFilePage rmRecordFileDialog = unfiledRecordsContainer.selectCreateNewUnfiledRecordsContainerFile().render();
        Assert.assertNotNull(rmRecordFileDialog);
        fileElectronicRecordToUnfiledRecordsContainer(drone, rmRecordFileDialog, "sourceRecord");
        unfiledRecordsContainer = unfiledRecordsContainer.render("sourceRecord");

        // copy test source folder
        navigateToPath("/test-5/source");
        List<FileDirectoryInfo> files = unfiledRecordsContainer.getFiles();
        Assert.assertEquals(files.size(), 1);
        FileDirectoryInfo sourceFile = files.get(0);
        sourceFile.selectMoveTo();
        RmCopyOrMoveUnfiledContentPage copyOrMoveUnfiledContentPage = new RmCopyOrMoveUnfiledContentPage(this.drone);
        copyOrMoveUnfiledContentPage.render();
        copyOrMoveUnfiledContentPage.selectPath("/test-5/destination");

        // ensure that we now don't have the folder in the source directory
        navigateToUnfiledRecords();
        navigateToPath("/test-5/source");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 0);

        // ensure that we now have the copied folder in the destination directory
        navigateToUnfiledRecords();
        navigateToPath("/test-5/destination");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 1);
        navigateToPath("/test-5/destination/sourceFolder");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 1);
    }

}