package org.diveintojee.poc.web;

import org.diveintojee.poc.domain.business.Facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * User: lgueye Date: 26/11/12 Time: 15:25
 */
@Component
@Path(StartConsumingClassifiedWriteCommandsResource.COLLECTION_RESOURCE_PATH)
public class StartConsumingClassifiedWriteCommandsResource {

    public static final String COLLECTION_RESOURCE_PATH = "/classifieds/startconsuming";

    @Autowired
    private Facade facade;

    @POST
    public void startConsuming() throws Throwable {
        facade.startConsumingClassifiedsWriteCommands();
    }

}
