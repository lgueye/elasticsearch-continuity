/**
 *
 */
package org.diveintojee.poc.web;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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

    public static final String SINGLE_RESOURCE_PATH = "/{accountId}";

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createAccount(final Classified classified) throws Throwable {
        final Long id = facade.createClassified(classified);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(ClassifiedsResource.SINGLE_RESOURCE_PATH)
                .build(String.valueOf(id));

        return Response.created(uri).build();

    }

}
