/*
 * Copyright (C) 2009-2011 Alfresco Software Limited.
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

package org.alfresco.module.org_alfresco_module_rm.job;

import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_rm.job.publish.PublishExecutor;
import org.alfresco.module.org_alfresco_module_rm.job.publish.PublishExecutorRegistry;
import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job to publish any pending updates on marked node references.
 * 
 * @author Roy Wetherall
 */
public class PublishUpdatesJob implements Job, RecordsManagementModel
{
    /** Logger */
    private static Log logger = LogFactory.getLog(PublishUpdatesJob.class);
    
    /** Node service */
    private NodeService nodeService;
    
    /** Search service */
    private SearchService searchService;
    
    /** Retrying transaction helper */
    private RetryingTransactionHelper retryingTransactionHelper;   
    
    /** Publish executor register */
    private PublishExecutorRegistry register;
    
    /** Behaviour filter */
    private BehaviourFilter behaviourFilter;
    
    /** Indicates whether the job bean has been initialised or not */
    private boolean initialised = false;   

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        // Initialise the service references
        initServices(context);
        
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Job Starting");
        }

        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                // Get a list of the nodes that have updates that need to be published
                List<NodeRef> nodeRefs = getUpdatedNodes();

                // Deal with each updated disposition action in turn
                for (NodeRef nodeRef : nodeRefs)
                {
                    // Mark the update node as publishing in progress
                    markPublishInProgress(nodeRef);                        
                    try
                    {
                        // Publish updates
                        publishUpdates(nodeRef);
                    }
                    finally
                    {
                        // Ensure the update node has either completed the publish or is marked as no longer in progress
                        unmarkPublishInProgress(nodeRef);
                    }                    
                }
                return null;
            };
        }, AuthenticationUtil.getSystemUserName());

        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Job Finished");
        }
    }
    
    /**
     * Get a list of the nodes with updates pending publish
     * @return  List<NodeRef>   list of node refences with updates pending publication
     */
    private List<NodeRef> getUpdatedNodes()
    {
        RetryingTransactionCallback<List<NodeRef>> execution = 
            new RetryingTransactionHelper.RetryingTransactionCallback<List<NodeRef>>()
            {
                @Override
                public List<NodeRef> execute() throws Throwable
                {
                    // Build the query string
                    StringBuilder sb = new StringBuilder();
                    sb.append("+ASPECT:\"rma:").append(ASPECT_UNPUBLISHED_UPDATE.getLocalName()).append("\" ");
                    sb.append("@rma\\:").append(PROP_PUBLISH_IN_PROGRESS.getLocalName()).append(":false "); 
                    String query = sb.toString();

                    if (logger.isDebugEnabled() == true)
                    {
                        logger.debug("Executing query " + query);
                    }
                    
                    // Execute query to find updates awaiting publishing
                    ResultSet results = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
                                                     SearchService.LANGUAGE_LUCENE, query);
                    List<NodeRef> resultNodes = results.getNodeRefs();
                    results.close();
                    
                    if (logger.isDebugEnabled() == true)
                    {
                        logger.debug("Found " + resultNodes.size() + " disposition action definitions updates awaiting publishing.");
                    }
                    
                    return resultNodes;
                }
            };
        return retryingTransactionHelper.doInTransaction(execution, true);
    }
    
    /**
     * Initialise service based on the job execution context
     * @param context   job execution context
     */
    private void initServices(JobExecutionContext context)
    {
        if (initialised == false)
        {
            // Get references to the required services
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();        
            nodeService = (NodeService)jobDataMap.get("nodeService");
            searchService = (SearchService)jobDataMap.get("searchService");
            retryingTransactionHelper = (RetryingTransactionHelper)jobDataMap.get("retryingTransactionHelper");
            register = (PublishExecutorRegistry)jobDataMap.get("publishExecutorRegistry");                                                                
            behaviourFilter = (BehaviourFilter)jobDataMap.get("behaviourFilter");
            initialised = true;
        }
    }
    
    /**
     * Mark the node as publish in progress.  This is often used as a marker to prevent any further updates 
     * to a node.
     * @param nodeRef   node reference
     */
    private void markPublishInProgress(final NodeRef nodeRef)
    {
        RetryingTransactionHelper.RetryingTransactionCallback<Void> execution = 
            new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    if (logger.isDebugEnabled() == true)
                    {
                        logger.debug("Marking updated node as publish in progress. (node=" + nodeRef.toString() + ")");
                    }
                    
                    behaviourFilter.disableBehaviour(nodeRef, TYPE_DISPOSITION_ACTION_DEFINITION);
                    try
                    {
                        // Mark the node as publish in progress
                        nodeService.setProperty(nodeRef, PROP_PUBLISH_IN_PROGRESS, true);
                    }
                    finally
                    {
                        behaviourFilter.enableBehaviour(nodeRef, TYPE_DISPOSITION_ACTION_DEFINITION);
                    }
                    return null;
                }
            };
        retryingTransactionHelper.doInTransaction(execution, false, true);
    }
    
    /**
     * Publish the updates made to the node.
     * @param nodeRef   node reference
     */
    private void publishUpdates(final NodeRef nodeRef)
    {
        RetryingTransactionHelper.RetryingTransactionCallback<Void> execution = 
            new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    behaviourFilter.disableBehaviour(nodeRef, TYPE_DISPOSITION_ACTION_DEFINITION);
                    try
                    {                    
                        // Get the update to value for the node
                        String updateTo = (String)nodeService.getProperty(nodeRef, PROP_UPDATE_TO);
                        
                        if (updateTo != null)
                        {                        
                            if (logger.isDebugEnabled() == true)
                            {
                                logger.debug("Node update to " +  updateTo + " (noderef=" + nodeRef.toString() + ")");
                            }
                            
                            // Get the publish executor
                            PublishExecutor executor = register.get(updateTo);
                            if (executor == null)
                            {
                                if (logger.isDebugEnabled() == true)
                                {
                                    logger.debug("Unable to find a corresponding publish executor. (noderef=" + nodeRef.toString() + ", updateTo=" + updateTo + ")");
                                }
                                throw new AlfrescoRuntimeException("Unable to find a corresponding publish executor. (noderef=" + nodeRef.toString() + ", updateTo=" + updateTo + ")");
                            }
                            
                            if (logger.isDebugEnabled() == true)
                            {
                                logger.debug("Attempting to publish updates. (nodeRef=" + nodeRef.toString() + ")");
                            }
                            
                            // Publish                    
                            executor.publish(nodeRef);
                        }
                        else
                        {
                            if (logger.isDebugEnabled() == true)
                            {
                                logger.debug("Unable to publish, because publish executor is not set.");
                            }
                        }                        
                        
                        // Remove the unpublished update aspect
                        nodeService.removeAspect(nodeRef, ASPECT_UNPUBLISHED_UPDATE);
                        
                        if (logger.isDebugEnabled() == true)
                        {
                            logger.debug("Publish updates complete. (nodeRef=" + nodeRef.toString() + ")");
                        }
                    }
                    finally
                    {
                        behaviourFilter.enableBehaviour(nodeRef, TYPE_DISPOSITION_ACTION_DEFINITION);
                    }
                    
                    return null;
                }
            };
        retryingTransactionHelper.doInTransaction(execution);
    }
    
    /**
     * Unmark node as publish in progress, assuming publish failed.
     * @param nodeRef   node reference
     */
    private void unmarkPublishInProgress(final NodeRef nodeRef)
    {
        RetryingTransactionHelper.RetryingTransactionCallback<Void> execution = 
            new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    behaviourFilter.disableBehaviour(nodeRef, TYPE_DISPOSITION_ACTION_DEFINITION);
                    try
                    {
                        // Assuming the node still has unpublished information, then unmark it in progress
                        if (nodeService.exists(nodeRef) == true &&
                            nodeService.hasAspect(nodeRef, ASPECT_UNPUBLISHED_UPDATE) == true)
                        {
                            if (logger.isDebugEnabled() == true)
                            {
                                logger.debug("Removing publish in progress marker from updated node, because update was not successful. (node=" + nodeRef.toString() + ")");
                            }
                            
                            nodeService.setProperty(nodeRef, PROP_PUBLISH_IN_PROGRESS, false);
                        }
                    }
                    finally
                    {
                        behaviourFilter.enableBehaviour(nodeRef, TYPE_DISPOSITION_ACTION_DEFINITION);
                    }
                    
                    return null;
                }
            };
        retryingTransactionHelper.doInTransaction(execution);
    }
}
