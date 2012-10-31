package org.diveintojee.poc.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;

/**
 * User: louis.gueye@gmail.com
 */
public abstract class BackendBaseSteps {

    protected Exchange exchange;

    protected BackendBaseSteps(Exchange exchange) {
        this.exchange = exchange;
    }

    public Exchange getExchange() {
        return this.exchange;
    }

    @Then("the response code should be \"$statusCode\"")
    public void expectStatusCode(@Named("statusCode") final int statusCode) {
        this.exchange.assertExpectedStatus(statusCode);
    }

    @Given("I provide \"<uid>\" uid and \"<password>\" password")
    public void authenticate(@Named("uid") final String uid, @Named("password") final String password) {
        this.exchange.setCredentials(uid, password);
    }

    @Given("I accept \"<responseLanguage>\" language")
    public void setAcceptLanguage(@Named("responseLanguage") final String requestedLanguage) {
        this.exchange.getRequest().setRequestedLanguage(requestedLanguage);
    }

    @Given("I accept \"<responseContentType>\" format")
    public void setResponseContentType(@Named("responseContentType") final String requestedType) {
        this.exchange.getRequest().setRequestedType(requestedType);
    }

    @Given("I send \"<requestContentType>\" format")
    public void setRequestContentType(@Named("requestContentType") final String type) {
        this.exchange.getRequest().setType(type);
    }

}
