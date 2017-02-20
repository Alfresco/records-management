/*
 * Copyright (C) 2005-2017 Alfresco Software Limited.
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

package org.alfresco.bm.dataload;

import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType.CONTENT_TYPE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.rm.role.RMRole;
import org.alfresco.bm.event.AbstractEventProcessor;
import org.alfresco.bm.file.TestFileService;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.bm.user.UserData;
import org.alfresco.bm.user.UserDataService;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentProperties;
import org.alfresco.rest.rm.community.requests.igCoreAPI.FilePlanComponentAPI;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class RMBaseEventProcessor extends AbstractEventProcessor implements RMEventConstants
{
    protected FileFolderService fileFolderService;
    protected TestFileService testFileService;

    @Autowired
    protected UserDataService userDataService;

    @Autowired
    protected SiteDataService siteDataService;

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setTestFileService(TestFileService testFileService)
    {
        this.testFileService = testFileService;
    }

    public void createFilePlanComponent(FolderData folder, FilePlanComponentAPI api,
                FilePlanComponent parentFilePlanComponent, int componentsToCreate, String nameIdentifier, String type, String context,
                long loadFilePlanComponentDelay) throws Exception // to-check: type of exception
    {
        String unique;

        String folderPath = folder.getPath();
        for (int i = 0; i < componentsToCreate; i++)
        {
            unique = UUID.randomUUID().toString();
            String newfilePlanComponentName = nameIdentifier + unique;
            String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;

            try
            {
                // Build filePlan component records folder properties
                FilePlanComponent filePlanComponentModel = FilePlanComponent.builder()
                    .name(newfilePlanComponentName)
                    .nodeType(type)
                    .properties(FilePlanComponentProperties.builder()
                            .title(newfilePlanComponentTitle)
                            .description(EMPTY)
                            .build())
                    .build();

                FilePlanComponent filePlanComponent = api.createFilePlanComponent(filePlanComponentModel, parentFilePlanComponent.getId(), "include=path");

                String newfilePlanComponentId = filePlanComponent.getId();
                fileFolderService.createNewFolder(newfilePlanComponentId, context,
                            folderPath + "/" + newfilePlanComponentName);
                TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, componentsToCreate);
    }

    public FolderData createFilePlanComponentWithFixedName(FolderData folder, FilePlanComponentAPI api,
                FilePlanComponent parentFilePlanComponent, String name, String type, String context) throws Exception // to-check: type of exception
    {
        FolderData createdFolder = null;
        String folderPath = folder.getPath();
        String newfilePlanComponentTitle = "title: " + name;
        try
        {
            // Build filePlan component records folder properties
            FilePlanComponent filePlanComponentModel = FilePlanComponent.builder()
                        .name(name)
                        .nodeType(type)
                        .properties(FilePlanComponentProperties.builder()
                                    .title(newfilePlanComponentTitle)
                                    .description(EMPTY)
                                    .build())
                        .build();

            FilePlanComponent filePlanComponent = api.createFilePlanComponent(filePlanComponentModel, parentFilePlanComponent.getId());

            String newfilePlanComponentId = filePlanComponent.getId();
            fileFolderService.createNewFolder(newfilePlanComponentId, context,
                        folderPath + "/" + name);
            // Increment counts
            fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
            createdFolder = fileFolderService.getFolder(newfilePlanComponentId);
        }
        catch (Exception e)
        {
            throw e;
        }
        return createdFolder;
    }

    public void createRecord(FolderData folder, FilePlanComponentAPI api,
                FilePlanComponent parentFilePlanComponent, int componentsToCreate, String nameIdentifier, String type,
                long loadFilePlanComponentDelay) throws Exception // to-check: type of exception
    {
        String unique;

        String folderPath = folder.getPath();
        for (int i = 0; i < componentsToCreate; i++)
        {
            unique = UUID.randomUUID().toString();
            String newfilePlanComponentName = nameIdentifier + unique;
            String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;

            try
            {
                // Build filePlan component records folder properties
                FilePlanComponent filePlanComponentModel = FilePlanComponent.builder()
                        .name(newfilePlanComponentName)
                        .nodeType(type)
                        .properties(FilePlanComponentProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                        .build();

                api.createFilePlanComponent(filePlanComponentModel, parentFilePlanComponent.getId());
                TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        // Increment counts
        fileFolderService.incrementFileCount(folder.getContext(), folderPath, componentsToCreate);
    }

    public void uploadElectronicRecord(FolderData folder, FilePlanComponentAPI api,
                FilePlanComponent parentFilePlanComponent, int componentsToCreate, String nameIdentifier,
                long loadFilePlanComponentDelay) throws Exception // to-check: type of exception
    {
        String unique;

        String folderPath = folder.getPath();
        for (int i = 0; i < componentsToCreate; i++)
        {
            File file = testFileService.getFile();
            if (file == null)
            {
                throw new RuntimeException("No test files exist for upload: " + testFileService);
            }
            unique = UUID.randomUUID().toString();
            String newfilePlanComponentName = nameIdentifier + unique + "-" + file.getName();
            String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;
            try
            {
                // Build filePlan component records folder properties
                FilePlanComponent filePlanComponentModel = FilePlanComponent.builder()
                        .name(newfilePlanComponentName)
                        .nodeType(CONTENT_TYPE.toString())
                        .properties(FilePlanComponentProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                        .build();

                api.createElectronicRecord(filePlanComponentModel, file, parentFilePlanComponent.getId());
                TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);
                fileFolderService.incrementFileCount(folder.getContext(), folderPath, 1);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
    }

    public HashMap<FolderData, Integer> distributeNumberOfRecords(List<FolderData> listOfFolders, int numberOfRecords)
    {
        HashMap<FolderData, Integer> mapOfRecordsPerFolder = new HashMap<FolderData,Integer>();
        int[] generateRandomValues = generateRandomValues(listOfFolders.size(), numberOfRecords);
        int counter = 0;
        for(FolderData folder : listOfFolders)
        {
            int records = (int)(folder.getFileCount() + generateRandomValues[counter]);
            mapOfRecordsPerFolder.put(folder, records);
            counter++;
        }
        return mapOfRecordsPerFolder;
    }

    /**
     * Obtains all record folders underneath specified parent if the parent is a category or the parent itself if it is a record folder
     *
     * @param parentFolder
     * @return all record folders underneath specified parent if the parent is a category or the parent itself if it is a record folder
     */
    protected Set<FolderData> getRecordFolders(FolderData parentFolder)
    {
        LinkedHashSet<FolderData> result = new LinkedHashSet<FolderData>();
        String context = parentFolder.getContext();
        if(RECORD_CATEGORY_CONTEXT.equals(context))
        {
            List<FolderData> directCategoryChildren = getDirectChildrenByContext(parentFolder, RECORD_CATEGORY_CONTEXT);
            if (directCategoryChildren.size() > 0)
            {
                for (FolderData childFolder : directCategoryChildren)
                {
                    result.addAll(getRecordFolders(childFolder));
                }
            }

            List<FolderData> directRecordFolderChildren = getDirectChildrenByContext(parentFolder, RECORD_FOLDER_CONTEXT);
            if (directRecordFolderChildren.size() > 0)
            {
                result.addAll(directRecordFolderChildren);
            }
        }
        else if(RECORD_FOLDER_CONTEXT.equals(context))
        {
            result.add(parentFolder);
        }
        return result;
    }

    private List<FolderData> getDirectChildrenByContext(FolderData parentFolder, String context)
    {
        int skip = 0;
        int limit = 100;
        List<FolderData> directChildren = new ArrayList<FolderData>();
        List<FolderData> childFolders = fileFolderService.getChildFolders(context, parentFolder.getPath(), skip, limit);
        while (childFolders.size() > 0)
        {
            directChildren.addAll(childFolders);
            skip += limit;
            childFolders = fileFolderService.getChildFolders(context, parentFolder.getPath(), skip, limit);
        }
        return directChildren;
    }

    private int[] generateRandomValues(int numberOfFolders, int numberOfRecords)
    {
        int[] aux = new int[numberOfFolders+1];
        int[] generatedValues = new int[numberOfFolders];
        Random r = new Random();
        for(int i = 1;i < numberOfFolders;i++)
        {
            aux[i] = r.nextInt(numberOfRecords)+1;
        }
        aux[0] = 0;
        aux[numberOfFolders] = numberOfRecords;
        Arrays.sort(aux);
        for(int i = 0;i < numberOfFolders;i++)
        {
            generatedValues[i] = aux[i+1] - aux[i];
        }
        return generatedValues;
    }

    /**
     * Helper method that parses a string representing a file path and returns a list of element names
     * @param path the file path represented as a string
     * @return a list of file path element names
     */
    public List<String> getPathElements(String path)
    {
        final List<String> pathElements = new ArrayList<>();
        if (path != null && path.trim().length() > 0)
        {
            // There is no need to check for leading and trailing "/"
            final StringTokenizer tokenizer = new StringTokenizer(path, "/");
            while (tokenizer.hasMoreTokens())
            {
                pathElements.add(tokenizer.nextToken().trim());
            }
        }
        return pathElements;
    }

    public List<FolderData> initialiseFoldersToExistingStructure(String context)
    {
        List<FolderData> existingFolderStructure = new ArrayList<FolderData>();
        int skip = 0;
        int limit = 100;
        List<FolderData> emptyFolders = fileFolderService.getFoldersByCounts(
                    context,
                    null, null,
                    null, null,
                    null, null,
                    skip, limit);
        while(emptyFolders.size() > 0)
        {
            existingFolderStructure.addAll(emptyFolders);
            skip += limit;
            emptyFolders = fileFolderService.getFoldersByCounts(
                        context,
                        null, null,
                        null, null,
                        null, null,
                        skip, limit);
        }
        return existingFolderStructure;
    }

    /**
     * Attempt to find a user to use.<br/>
     * The site ID will be used to find a valid site manager or collaborator.
     */
    public UserData getUser(Log logger)
    {
        // Check
        SiteData siteData = siteDataService.getSite(PATH_SNIPPET_RM_SITE_ID);
        if (siteData == null)
        {
            throw new IllegalStateException("Unable to find site '" + PATH_SNIPPET_RM_SITE_ID + "'");
        }
        SiteMemberData siteMember = siteDataService.randomSiteMember(PATH_SNIPPET_RM_SITE_ID, DataCreationState.Created, null, RMRole.ADMINISTRATOR.name());
        if (siteMember == null)
        {
            throw new IllegalStateException("Unable to find a collaborator or manager for site: " + PATH_SNIPPET_RM_SITE_ID);
        }
        String username = siteMember.getUsername();
        // Retrieve the user data
        UserData user = userDataService.findUserByUsername(username);
        if (user == null)
        {
            throw new IllegalStateException("Unable to find a user '" + username + "' linked to site: " + PATH_SNIPPET_RM_SITE_ID);
        }
        // Done
        if (logger.isDebugEnabled())
        {
            logger.debug("Found site member '" + username + "'");
        }
        return user;
    }
}