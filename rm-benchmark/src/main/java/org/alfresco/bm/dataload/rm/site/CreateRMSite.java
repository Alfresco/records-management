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

import static org.alfresco.bm.data.DataCreationState.Created;
import static org.alfresco.bm.data.DataCreationState.Failed;
import static org.alfresco.bm.data.DataCreationState.Scheduled;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_ONLY_DB_LOAD;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_SITE_ID;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_SITE_MANAGER;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.RM_SITE_DESC;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.RM_SITE_TITLE;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.FILE_PLAN_ALIAS;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.HOLDS_ALIAS;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.TRANSFERS_ALIAS;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.UNFILED_RECORDS_CONTAINER_ALIAS;
import static org.alfresco.rest.rm.community.model.site.RMSiteCompliance.STANDARD;
import static org.alfresco.rest.rm.community.util.ParameterCheck.mandatoryObject;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.mongodb.DBObject;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.event.AbstractEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
import org.alfresco.rest.rm.community.model.site.RMSite;
import org.alfresco.rest.rm.community.requests.igCoreAPI.FilePlanComponentAPI;
import org.alfresco.rest.rm.community.requests.igCoreAPI.RMSiteAPI;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RM Site creation event
 *
 * @author Tuna Aksoy
 * @since 1.0
 */
public class CreateRMSite extends AbstractEventProcessor
{
    @Autowired
    private RestAPIFactory restAPIFactory;

    @Autowired
    private SiteDataService siteDataService;

    @Autowired
    private FileFolderService fileFolderService;

    public static final String PATH_SNIPPET_SITES = "Sites";
    public static final String PATH_SNIPPET_FILE_PLAN = "documentLibrary";
    public static final String PATH_SNIPPET_UNFILED_RECORD_CONTAINER = "Unfiled Records";
    public static final String PATH_SNIPPET_TRANSFER_CONTAINER = "Transfers";
    public static final String PATH_SNIPPET_HOLD_CONTAINER = "Holds";

    public static final String DEFAULT_EVENT_NAME_SITE_CREATED = "rmSiteCreated";

    private String eventNameSiteCreated = DEFAULT_EVENT_NAME_SITE_CREATED;
    /**
     * Override the {@link #DEFAULT_EVENT_NAME_SITE_CREATED default} event name when the site is created
     */
    public void setEventNameSiteCreated(String eventNameSiteCreated)
    {
        this.eventNameSiteCreated = eventNameSiteCreated;
    }

    @Override
    public EventResult processEvent(Event event) throws Exception
    {
        mandatoryObject("event", event);

        DBObject dataObj = (DBObject) event.getData();
        if (dataObj == null)
        {
            throw new IllegalStateException("This processor requires data object with fields '" + FIELD_SITE_ID + ", " + FIELD_SITE_MANAGER + "'.");
        }

        String siteId = (String) dataObj.get(FIELD_SITE_ID);
        String siteManager = (String) dataObj.get(FIELD_SITE_MANAGER);
        Boolean onlyLoadInDb = (Boolean) dataObj.get(FIELD_ONLY_DB_LOAD);

        if (isBlank(siteId) || isBlank(siteManager))
        {
            return new EventResult("Requests data not complete for site creation: " + dataObj, false);
        }

        SiteData site = siteDataService.getSite(siteId);
        if (site == null)
        {
            return new EventResult("Site has been removed: " + siteId, false);
        }
        if (site.getCreationState() == Created)
        {
            return new EventResult("RM Site already exists in DB: " + site, false);
        }
        if (site.getCreationState() != Created && site.getCreationState() != Scheduled)
        {
            return new EventResult("Site state has changed: " + site, false);
        }

        // Start by marking them as failures in order to handle all eventualities
        siteDataService.setSiteCreationState(siteId, null, Failed);
        siteDataService.setSiteMemberCreationState(siteId, siteManager, Failed);

        String msg = null;
        try
        {
            RMSiteAPI rmSiteAPI = restAPIFactory.getRMSiteAPI(new UserModel(siteManager, siteManager));
            String guid = null;
            if (onlyLoadInDb == null)
            {
                RMSite siteModel = RMSite.builder().compliance(STANDARD).build();
                siteModel.setTitle(RM_SITE_TITLE);
                siteModel.setDescription(RM_SITE_DESC);

                RMSite rmSite = rmSiteAPI.createRMSite(siteModel);
                guid = rmSite.getGuid();
                msg = "Created site: " + siteId + " Site creator: " + siteManager;
            }
            else
            {
                RMSite alreadyCreatedRMSite = rmSiteAPI.getSite();
                guid = alreadyCreatedRMSite.getGuid();
                msg = "RM site already exists, just loading it in the DB.";
            }

            // Mark the site.
            siteDataService.setSiteCreationState(siteId, guid, Created);
            siteDataService.setSiteMemberCreationState(siteId, siteManager, Created);

//            while (!restAPIFactory.getRMSiteAPI(siteManager).existsRMSite()) {
//                continue;
//            }

            loadSpecialContainersInDB(siteId, siteManager);
            event = new Event(eventNameSiteCreated, null);
        }
        catch (Exception e)
        {
            // The creation failed
            throw e;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(msg);
        }

        return new EventResult(msg, event);
    }

    private void loadSpecialContainersInDB(String siteId, String siteManager) throws Exception
    {
        FilePlanComponentAPI filePlanComponentsAPI = restAPIFactory.getFilePlanComponentsAPI(new UserModel(siteManager, siteManager));
        FilePlanComponent filePlanComponent = filePlanComponentsAPI.getFilePlanComponent(FILE_PLAN_ALIAS);

        FolderData filePlan = new FolderData(
                filePlanComponent.getId(),                                   // already unique
                "",
                "/" + PATH_SNIPPET_SITES + "/" + siteId + "/" + PATH_SNIPPET_FILE_PLAN,
                0L, 0L);
        fileFolderService.createNewFolder(filePlan);

        //add Unfiled record container
        filePlanComponent = filePlanComponentsAPI.getFilePlanComponent(UNFILED_RECORDS_CONTAINER_ALIAS);

        FolderData unfiledRecordContainer = new FolderData(
                filePlanComponent.getId(),                                   // already unique
                "unfiled",
                "/" + PATH_SNIPPET_SITES + "/" + siteId + "/" + PATH_SNIPPET_FILE_PLAN + "/" + PATH_SNIPPET_UNFILED_RECORD_CONTAINER,
                0L, 0L);
        fileFolderService.createNewFolder(unfiledRecordContainer);

        //add Transfer container
        filePlanComponent = filePlanComponentsAPI.getFilePlanComponent(TRANSFERS_ALIAS);

        FolderData transferContainer = new FolderData(
                    filePlanComponent.getId(),                                   // already unique
                    "transfers",
                    "/" + PATH_SNIPPET_SITES + "/" + siteId + "/" + PATH_SNIPPET_FILE_PLAN + "/" + PATH_SNIPPET_TRANSFER_CONTAINER,
                    0L, 0L);
        fileFolderService.createNewFolder(transferContainer);

        //add Hold container
        filePlanComponent = filePlanComponentsAPI.getFilePlanComponent(HOLDS_ALIAS);

        FolderData holdContainer = new FolderData(
                    filePlanComponent.getId(),                                   // already unique
                    "holds",
                    "/" + PATH_SNIPPET_SITES + "/" + siteId + "/" + PATH_SNIPPET_FILE_PLAN + "/" + PATH_SNIPPET_HOLD_CONTAINER,
                    0L, 0L);
        fileFolderService.createNewFolder(holdContainer);
    }
}
