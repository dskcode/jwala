package com.siemens.cto.aem.ws.rest.v1.service.app;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.siemens.cto.aem.domain.model.app.Application;
import com.siemens.cto.aem.domain.model.group.Group;
import com.siemens.cto.aem.domain.model.id.Identifier;
import com.siemens.cto.aem.domain.model.jvm.Jvm;
import com.siemens.cto.aem.ws.rest.v1.provider.AuthenticatedUser;
import com.siemens.cto.aem.ws.rest.v1.provider.PaginationParamProvider;
import com.siemens.cto.aem.ws.rest.v1.service.app.impl.JsonCreateApplication;
import com.siemens.cto.aem.ws.rest.v1.service.app.impl.JsonUpdateApplication;

@Path("/applications")
@Produces(MediaType.APPLICATION_JSON)
public interface ApplicationServiceRest {

    @GET
    Response getApplications(@QueryParam("group.id") final Identifier<Group> aGroupId,
                             @BeanParam final PaginationParamProvider paginationParamProvider );

    @GET
    @Path("/{applicationId}")
    Response getApplication(@PathParam("applicationId") final Identifier<Application> anAppId);

    @GET
    @Path("/jvm/{jvmId}")
    Response findApplicationsByJvmId(@PathParam("jvmId") final Identifier<Jvm> aJvmId,
                                     @BeanParam final PaginationParamProvider paginationParamProvider);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createApplication(final JsonCreateApplication anAppToCreate,
                               @BeanParam final AuthenticatedUser aUser);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateApplication(final JsonUpdateApplication appsToUpdate,
                               @BeanParam final AuthenticatedUser aUser);

    @DELETE
    @Path("/{applicationId}")
    Response removeApplication(@PathParam("applicationId") final Identifier<Application> anAppToRemove,
                               @BeanParam final AuthenticatedUser aUser);

    @POST
    @Path("/{applicationId}/war")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    Response uploadWebArchive(@PathParam("applicationId") final Identifier<Application> anAppToGet,
                              @BeanParam final AuthenticatedUser aUser);

    @DELETE
    @Path("/{applicationId}/war")
    Response deleteWebArchive(@PathParam("applicationId") final Identifier<Application> anAppToGet,
                              @BeanParam final AuthenticatedUser aUser);

}
