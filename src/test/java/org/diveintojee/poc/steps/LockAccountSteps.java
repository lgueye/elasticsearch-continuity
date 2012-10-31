/**
 *
 */
package org.diveintojee.poc.steps;

import org.diveintojee.poc.web.WebConstants;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import javax.ws.rs.core.UriBuilder;

import fr.midipascher.domain.ResponseError;
import fr.midipascher.web.WebConstants;
import fr.midipascher.web.resources.AccountsResource;

/**
 * @author louis.gueye@gmail.com
 */
public class LockAccountSteps extends BackendBaseSteps {

    private static final String LOCK_URI = UriBuilder.fromPath(AccountsResource.COLLECTION_RESOURCE_PATH)
            .path("5")
            .path("lock").build().toString();

    /**
     * @param exchange
     */
    public LockAccountSteps(Exchange exchange) {
        super(exchange);
    }

    @Then("the message should be \"<message>\"")
    public void expectedMessage(@Named("message") final String message) {
        this.exchange.assertExpectedMessage(ResponseError.class, message);
    }

    @When("I send a \"lock account\" request with wrong id \"<wrong_id>\"")
    public void sendLockAccountRequestWithWrongId(@Named("wrong_id") final Long id) {
        final String uri = WebConstants.BACKEND_PATH + AccountsResource.COLLECTION_RESOURCE_PATH + "/" + id + "/lock";
        this.exchange.getRequest().setUri(uri);
        this.exchange.lockEntity();
    }

    @When("I send a valid \"lock account\" request")
    public void sendValidLockAccountRequest() {
        this.exchange.getRequest().setUri(LOCK_URI);
        this.exchange.lockEntity();
    }

}
