/**
 *
 */
package org.diveintojee.poc.steps;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.persistence.search.ClassifiedSearchFieldsRegistry;
import org.diveintojee.poc.web.ClassifiedsResource;
import org.diveintojee.poc.web.SearchClassifiedsResource;
import org.diveintojee.poc.web.WebConstants;
import org.hamcrest.Matchers;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.OutcomesTable;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author louis.gueye@gmail.com
 */
public class ContinuitySteps extends BackendBaseSteps {

    private static final String SEARCH_URI = UriBuilder.fromPath(WebConstants.BACKEND_PATH)
            .path(SearchClassifiedsResource.class).build().toString();

    private List<URI> createdClassifiedUris = Lists.newArrayList();

    private static final String CREATE_URI = UriBuilder.fromPath(WebConstants.BACKEND_PATH)
            .path(ClassifiedsResource.COLLECTION_RESOURCE_PATH).build().toString();

    /**
     * @param exchange
     */
    public ContinuitySteps(Exchange exchange) {
        super(exchange);
    }

    @Given("I create the following classifieds: $table")
    public void beforeStep(ExamplesTable table) throws IOException {
        Exchange exchange = new Exchange();
        for (int i = 0; i < table.getRowCount(); i++) {
            Map<String, String> row = table.getRow(i);
            Classified classified = fromRow(row);
            exchange.getRequest().setBody(classified);
            exchange.setCredentials("louis@rmgr.com", "secret");
            exchange.getRequest().setUri(CREATE_URI);
            exchange.createEntity();
            final URI uri = exchange.getLocation();
            createdClassifiedUris.add(uri);

        }
    }

    private Classified fromRow(Map<String, String> row) throws IOException {
        Classified classified = new Classified();
        final String title = row.get(ClassifiedSearchFieldsRegistry.TITLE);
        classified.setTitle(title);
        final String description = row.get(ClassifiedSearchFieldsRegistry.DESCRIPTION);
        classified.setDescription(description);
        return classified;
    }


    @When("I search for classifieds which \"$criterion\" matches \"$value\"")
    public void searchClassifiedByName(@Named("criterion") String criterion, @Named("value") String value) {
        Classified criteria = new Classified();
        if (ClassifiedSearchFieldsRegistry.TITLE.equalsIgnoreCase(criterion)) criteria.setTitle(value);
        else if (ClassifiedSearchFieldsRegistry.DESCRIPTION.equalsIgnoreCase(criterion)) criteria.setDescription(value);
        this.exchange.getRequest().setBody(criteria);
        this.exchange.getRequest().setType(MediaType.APPLICATION_XML);
        this.exchange.getRequest().setRequestedType(MediaType.APPLICATION_XML);
        this.exchange.getRequest().setUri(SEARCH_URI);
        this.exchange.findEntityByCriteria();

    }

    @Then("I should get the following classifieds: $table")
    public void theValuesReturnedAre(ExamplesTable table) {
        List<Classified> classifieds = this.exchange.classifiedsFromResponse();
        assertEquals(table.getRowCount(), classifieds.size());
        for (int i = 0; i < table.getRowCount(); i++) {
            Map<String, String> actualRow = actualRow(classifieds.get(i)); // obtained from another step invocation
            OutcomesTable outcomes = new OutcomesTable();
            Map<String, String> expectedRow = table.getRow(i);
            for (String key : expectedRow.keySet()) {
                outcomes.addOutcome(key, actualRow.get(key), Matchers.equalTo(expectedRow.get(key)));
            }
            outcomes.verify();
        }

    }

    private Map<String, String> actualRow(Classified classified) {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<String, String>();
        builder.put(ClassifiedSearchFieldsRegistry.TITLE, classified.getTitle());
        builder.put(ClassifiedSearchFieldsRegistry.DESCRIPTION, classified.getDescription());
        return builder.build();
    }

    @AfterStory
    public void afterStory() {
        Exchange exchange = new Exchange();
        exchange.setCredentials("louis@rmgr.com", "secret");
        for (URI createdClassifiedUri : createdClassifiedUris) {
            exchange.getRequest().setUri(createdClassifiedUri.toString());
            exchange.deleteEntity();
        }
    }
}
