package org.diveintojee.poc.steps;

import com.google.common.base.Strings;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.diveintojee.poc.domain.ResponseError;
import org.junit.Assert;

import java.net.URI;
import java.util.List;
import java.util.ResourceBundle;

import fr.midipascher.domain.FoodSpecialty;
import fr.midipascher.domain.ResponseError;
import fr.midipascher.domain.Restaurant;

/**
 * User: louis.gueye@gmail.com Date: 24/08/12 Time: 01:14
 */
public class Exchange {

    private static final String baseEndPoint = ResourceBundle.getBundle("stories-context").getString("baseEndPoint");

    /**
     *
     */
    public Exchange() {
        this.request = new Request();
        final DefaultClientConfig config = new DefaultApacheHttpClient4Config();
        this.jerseyClient = ApacheHttpClient4.create(config);
        this.jerseyClient.addFilter(new LoggingFilter());
        config.getClasses().add(JacksonJsonProvider.class);
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
    }

    private final Request request;
    private final Client jerseyClient;
    private ClientResponse clientResponse;

    /**
     * @param expected
     */
    public void assertExpectedStatus(int expected) {
        Assert.assertEquals(expected, this.clientResponse.getStatus());
    }

    /**
     * @param clazz
     */
    public void assertNewResource(Class<?> clazz) {
        this.clientResponse = readLocation();
        Assert.assertNotNull(this.clientResponse.getEntity(clazz));

    }

    /**
     * @return
     */
    public ClientResponse readLocation() {
        return this.jerseyClient.resource(this.clientResponse.getLocation()).accept(this.request.getRequestedType())
                .acceptLanguage(this.request.getRequestedLanguage()).get(ClientResponse.class);

    }

    /**
     * @return
     */
    public URI getLocation() {
      return this.clientResponse.getLocation();
    }

    /**
     * @return
     */
    public void createEntity() {
        final URI uri = newURI(this.request.getUri());
        this.clientResponse = this.jerseyClient.resource(uri).type(this.request.getType())
                .accept(this.request.getRequestedType()).acceptLanguage(this.request.getRequestedLanguage())
                .post(ClientResponse.class, this.request.getBody());
    }

    /**
     * @param uriAsString
     * @return
     */
    private URI newURI(String uriAsString) {
        if (Strings.isNullOrEmpty(uriAsString)) return URI.create(baseEndPoint);
        if (!uriAsString.startsWith("/")) return URI.create(uriAsString);
        return URI.create(baseEndPoint + uriAsString);
    }

    /**
     * @param uid
     * @param password
     */
    public void setCredentials(String uid, String password) {
        this.request.setUid(uid);
        this.jerseyClient.removeAllFilters();
        this.jerseyClient.addFilter(new LoggingFilter());
        this.jerseyClient.addFilter(new HTTPBasicAuthFilter(uid, password));
    }

    /**
     * @return
     */
    public Request getRequest() {
        return this.request;
    }

    public void findEntityByCriteria() {
        final URI uri = newURI(this.request.getUri());
        this.clientResponse = this.jerseyClient.resource(uri).type(this.request.getType())
                .accept(this.request.getRequestedType()).acceptLanguage(this.request.getRequestedLanguage())
                .post(ClientResponse.class, this.request.getBody());
    }

    /**
     */
    public void assertExpectedMessage(Class<?> clazz, String expected) {

        Object message = this.clientResponse.getEntity(clazz);

        String actual = null;

        if (message instanceof String) actual = (String) message;
        else
            actual = ((ResponseError) message).getMessage();

        Assert.assertEquals(expected, actual);
    }

    public void readURI() {
        final URI uri = newURI(this.request.getUri());
        this.clientResponse = this.jerseyClient.resource(uri).accept(this.request.getRequestedType())
                .acceptLanguage(this.request.getRequestedLanguage()).get(ClientResponse.class);
    }

    public void updateEntity() {
        final URI uri = newURI(this.request.getUri());
        this.clientResponse = this.jerseyClient.resource(uri).type(this.request.getType())
                .accept(this.request.getRequestedType()).acceptLanguage(this.request.getRequestedLanguage())
                .put(ClientResponse.class, this.request.getBody());

    }

    public void inactivateEntity() {
        final URI uri = newURI(this.request.getUri());
        this.clientResponse = this.jerseyClient.resource(uri).accept(this.request.getRequestedType())
                .acceptLanguage(this.request.getRequestedLanguage()).post(ClientResponse.class);
    }

    public void deleteEntity() {
        final URI uri = newURI(this.request.getUri());
        this.clientResponse = this.jerseyClient.resource(uri).accept(this.request.getRequestedType())
                .acceptLanguage(this.request.getRequestedLanguage()).delete(ClientResponse.class);
    }

    /**
     *
     */
    public void lockEntity() {
        inactivateEntity();
    }

    public List<Restaurant> restaurantsFromResponse() {
        return this.clientResponse.getEntity(new GenericType<List<Restaurant>>() {});
    }
    public List<FoodSpecialty> foodSpcialtiesFromResponse() {
        return this.clientResponse.getEntity(new GenericType<List<FoodSpecialty>>() {});
    }
}
