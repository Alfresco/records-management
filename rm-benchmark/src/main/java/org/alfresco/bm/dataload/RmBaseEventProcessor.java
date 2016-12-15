package org.alfresco.bm.dataload;

import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType.CONTENT_TYPE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.event.AbstractEventProcessor;
import org.alfresco.bm.file.TestFileService;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentProperties;
import org.alfresco.rest.rm.community.requests.FilePlanComponentAPI;

public abstract class RmBaseEventProcessor extends AbstractEventProcessor implements RMEventConstants
{
    protected FileFolderService fileFolderService;
    protected TestFileService testFileService;

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

                FilePlanComponent filePlanComponent = api.createFilePlanComponent(filePlanComponentModel, parentFilePlanComponent.getId());

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
}