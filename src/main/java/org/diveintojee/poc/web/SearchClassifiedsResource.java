package org.diveintojee.poc.web;

import org.apache.commons.collections.CollectionUtils;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Facade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
@Component
@Path(SearchClassifiedsResource.RESOURCE_PATH)
@Scope("request")
public class SearchClassifiedsResource {

    public static final String RESOURCE_PATH = "/classifieds/search";

    @Autowired
    private Facade facade;

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchClassifiedsResource.class);

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response find(final Classified criteria) throws Throwable {

        final List<Classified> results = facade.findClassifiedsByCriteria(criteria);

        final GenericEntity<List<Classified>> entity = new GenericEntity<List<Classified>>(results) {
        };

        if (CollectionUtils.isEmpty(results)) {
            SearchClassifiedsResource.LOGGER.info("No results found");
        }

        return Response.ok(entity).build();

    }
}
