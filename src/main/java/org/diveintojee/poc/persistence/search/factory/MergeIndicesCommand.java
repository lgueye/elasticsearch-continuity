package org.diveintojee.poc.persistence.search.factory;

import org.elasticsearch.client.Client;
import org.springframework.stereotype.Component;

/**
 * @author louis.gueye@gmail.com
 */
@Component
public class MergeIndicesCommand {

    public void execute(Client client, String configFormat) {
        throw new UnsupportedOperationException("Not yet supported");
    }
}
