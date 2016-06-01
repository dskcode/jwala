package com.siemens.cto.aem.ws.rest.v1.service.resource.impl;

import com.siemens.cto.aem.common.domain.model.fault.AemFaultType;
import com.siemens.cto.aem.common.exception.FaultCodeException;
import com.siemens.cto.aem.service.exception.ResourceServiceException;
import com.siemens.cto.aem.service.resource.ResourceService;
import com.siemens.cto.aem.ws.rest.v1.provider.AuthenticatedUser;
import com.siemens.cto.aem.ws.rest.v1.response.ResponseBuilder;
import com.siemens.cto.aem.ws.rest.v1.service.resource.ResourceServiceRest;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ResourceServiceRest} implementation.
 *
 * Created by z003e5zv on 3/16/2015.
 */
public class ResourceServiceRestImpl implements ResourceServiceRest {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceServiceRestImpl.class);
    private static final int CREATE_TEMPLATE_EXPECTED_NUM_OF_ATTACHMENTS = 2;
    private static final String JSON_FILE_EXTENSION = ".json";

    private final ResourceService resourceService;

    public ResourceServiceRestImpl(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Response findResourceInstanceByGroup(String groupName) {
        return ResponseBuilder.ok(resourceService.getResourceInstancesByGroupName(groupName));
    }

    @Override
    public Response findResourceInstanceByNameGroup(final String name, final String groupName) {
        return ResponseBuilder.ok(resourceService.getResourceInstancesByGroupName(groupName));
    }

    @Override
    public Response createResourceInstance(JsonResourceInstance aResourceInstanceToCreate, AuthenticatedUser aUser) {
        return ResponseBuilder.ok(this.resourceService.createResourceInstance(aResourceInstanceToCreate.getCommand(), aUser.getUser()));
    }

    @Override
    public Response updateResourceInstanceAttributes(final String name, final String groupName, JsonResourceInstance aResourceInstanceToUpdate, AuthenticatedUser aUser) {
        return ResponseBuilder.ok(this.resourceService.updateResourceInstance(groupName, name, aResourceInstanceToUpdate.getCommand(), aUser.getUser()));
    }

    @Override
    public Response removeResourceInstance( final String name, final String groupName) {
        this.resourceService.deleteResourceInstance(groupName, name);
        return ResponseBuilder.ok();
    }

    @Override
    public Response removeResources(String groupName, List<String> resourceNames) {
        try {
            resourceService.deleteResources(groupName, resourceNames);
            return ResponseBuilder.ok();
        } catch (RuntimeException e) {
            LOGGER.error("Could not remove resources {}", resourceNames, e);
            return ResponseBuilder.notOk(Response.Status.INTERNAL_SERVER_ERROR,
                    new FaultCodeException(AemFaultType.PERSISTENCE_ERROR, e.getMessage()));
        }
    }

    @Override
    // TODO: Refactor resourceService.createTemplate(inputStreams[0], inputStreams[1]) since it looks ambiguous.
    public Response createTemplate(final List<Attachment> attachments, final String targetName, final AuthenticatedUser user) {
        try {
            List<Attachment> filteredAttachments = new ArrayList<>();
            for(Attachment attachment:attachments) {
                if(attachment.getDataHandler().getName() != null) {
                    filteredAttachments.add(attachment);
                }
            }
            if (filteredAttachments.size() == CREATE_TEMPLATE_EXPECTED_NUM_OF_ATTACHMENTS) {
                InputStream metadataInputStream = null;
                InputStream templateInputStream = null;
                for (Attachment attachment:filteredAttachments) {
                    final DataHandler handler = attachment.getDataHandler();
                    try {
                        LOGGER.debug("filename is {}", handler.getName());
                        if(handler.getName().toLowerCase().endsWith(JSON_FILE_EXTENSION)) {
                            metadataInputStream = attachment.getDataHandler().getInputStream();
                        } else {
                            templateInputStream = attachment.getDataHandler().getInputStream();
                        }
                    } catch (final IOException ioe) {
                        LOGGER.error("Create template failed!", ioe);
                        return ResponseBuilder.notOk(Response.Status.INTERNAL_SERVER_ERROR,
                                new FaultCodeException(AemFaultType.IO_EXCEPTION, ioe.getMessage()));
                    }
                }
                return ResponseBuilder.created(resourceService.createTemplate(metadataInputStream, templateInputStream, targetName));
            } else {
                return ResponseBuilder.notOk(Response.Status.INTERNAL_SERVER_ERROR, new FaultCodeException(
                        AemFaultType.INVALID_NUMBER_OF_ATTACHMENTS,
                        "Invalid number of attachments! 2 attachments is expected by the service."));
            }
        } catch (final ResourceServiceException rse) {
            LOGGER.error("Remove template failed!", rse);
            return ResponseBuilder.notOk(Response.Status.INTERNAL_SERVER_ERROR,
                    new FaultCodeException(AemFaultType.SERVICE_EXCEPTION, rse.getMessage()));
        }
    }

    @Override
    public Response removeTemplate(final String name) {
        try {
            return ResponseBuilder.ok(resourceService.removeTemplate(name));
        } catch (final ResourceServiceException rse) {
            LOGGER.error("Remove template failed!", rse);
            return ResponseBuilder.notOk(Response.Status.INTERNAL_SERVER_ERROR,
                    new FaultCodeException(AemFaultType.PERSISTENCE_ERROR, rse.getMessage()));
        }
    }

    @Override
    public Response getResourceAttrData() {
        return ResponseBuilder.ok(resourceService.generateResourceGroup());
    }

    @Override
    public Response getResourceTopology() {
        return ResponseBuilder.ok(resourceService.generateResourceGroup());
    }

    @Override
    public Response getApplicationResourceNames(final String groupName, final String appName) {
        return ResponseBuilder.ok(resourceService.getApplicationResourceNames(groupName, appName));
    }

    @Override
    public Response getAppTemplate(final String groupName, final String appName, final String templateName) {
        return ResponseBuilder.ok(resourceService.getAppTemplate(groupName, appName, templateName));
    }

    @Override
    public Response checkFileExists(final String groupName, final String jvmName, final String webappName, final String webserverName, final String fileName) {
        return ResponseBuilder.ok(resourceService.checkFileExists(groupName, jvmName, webappName, webserverName, fileName));
    }
}
