/**
 *
 */
package org.diveintojee.poc.web;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author louis.gueye@gmail.com
 */
@Component
@Path(ClassifiedsResource.COLLECTION_RESOURCE_PATH)
public class ClassifiedsResource {

    @Autowired
    private Facade facade;

    @Context
    private UriInfo uriInfo;

    public static final String COLLECTION_RESOURCE_PATH = "/classifieds";

    public static final String SINGLE_RESOURCE_PATH = "/{classifiedId}";

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(final Classified classified) throws Throwable {
        final Long id = facade.createClassified(classified);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(ClassifiedsResource.SINGLE_RESOURCE_PATH)
                .build(String.valueOf(id));
        return Response.created(uri).build();

    }

    @DELETE
    @Path(SINGLE_RESOURCE_PATH)
    public Response delete(@PathParam(value = "classifiedId") final Long classifiedId) throws Throwable {

        facade.deleteClassified(classifiedId);
        return Response.ok().build();

    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(SINGLE_RESOURCE_PATH)
    public Response get(@PathParam(value = "classifiedId") final Long classifiedId) throws Throwable {

        final Classified classified = facade.readClassified(classifiedId);
        return Response.ok(classified).build();

    }

}
