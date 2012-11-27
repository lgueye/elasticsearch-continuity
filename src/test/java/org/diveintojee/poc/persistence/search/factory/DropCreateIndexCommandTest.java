package org.diveintojee.poc.persistence.search.factory;

import com.google.common.collect.Maps;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author louis.gueye@gmail.com
 */
public class DropCreateIndexCommandTest {

    private DropCreateIndexCommand underTest;

    @Before
    public void before() {
        underTest = new DropCreateIndexCommand();
    }

    @Test
    public void resolveNewIndexNameShouldSucceed() {
        String oldIndexName = "index-a";
        String indexRootName = "index";
        String newIndexName = underTest.resolveNewIndexName(oldIndexName, indexRootName);
        assertEquals(indexRootName + "-b", newIndexName);
    }

    @Test(expected = IllegalStateException.class)
    public void resolveOlIndexNameShouldThrowIllegalStateException() {
        String indexRootName = "index";
        IndicesAdminClient indicesAdminClient = mock(IndicesAdminClient.class);
        IndicesExistsRequestBuilder indexAExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-a")).thenReturn(indexAExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureA = mock(ListenableActionFuture.class);
        when(indexAExistsRequestBuilder.execute()).thenReturn(listenableActionFutureA);
        IndicesExistsResponse indexAExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureA.actionGet()).thenReturn(indexAExistsResponse);
        when(indexAExistsResponse.exists()).thenReturn(true);

        IndicesExistsRequestBuilder indexBExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-b")).thenReturn(indexBExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureB = mock(ListenableActionFuture.class);
        when(indexBExistsRequestBuilder.execute()).thenReturn(listenableActionFutureB);
        IndicesExistsResponse indexBExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureB.actionGet()).thenReturn(indexBExistsResponse);
        when(indexBExistsResponse.exists()).thenReturn(true);

        underTest.resolveOldIndexName(indicesAdminClient, indexRootName);
    }

    @Test
    public void resolveOlIndexNameShouldDefaultToIndexB() {
        String indexRootName = "index";
        IndicesAdminClient indicesAdminClient = mock(IndicesAdminClient.class);
        IndicesExistsRequestBuilder indexAExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-a")).thenReturn(indexAExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureA = mock(ListenableActionFuture.class);
        when(indexAExistsRequestBuilder.execute()).thenReturn(listenableActionFutureA);
        IndicesExistsResponse indexAExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureA.actionGet()).thenReturn(indexAExistsResponse);
        when(indexAExistsResponse.exists()).thenReturn(false);

        IndicesExistsRequestBuilder indexBExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-b")).thenReturn(indexBExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureB = mock(ListenableActionFuture.class);
        when(indexBExistsRequestBuilder.execute()).thenReturn(listenableActionFutureB);
        IndicesExistsResponse indexBExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureB.actionGet()).thenReturn(indexBExistsResponse);
        when(indexBExistsResponse.exists()).thenReturn(false);

        String oldIndexName = underTest.resolveOldIndexName(indicesAdminClient, indexRootName);
        assertEquals(indexRootName + "-b", oldIndexName);
    }

    @Test
    public void resolveOlIndexNameShouldResolveToIndexB() {
        String indexRootName = "index";
        IndicesAdminClient indicesAdminClient = mock(IndicesAdminClient.class);
        IndicesExistsRequestBuilder indexAExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-a")).thenReturn(indexAExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureA = mock(ListenableActionFuture.class);
        when(indexAExistsRequestBuilder.execute()).thenReturn(listenableActionFutureA);
        IndicesExistsResponse indexAExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureA.actionGet()).thenReturn(indexAExistsResponse);
        when(indexAExistsResponse.exists()).thenReturn(false);

        IndicesExistsRequestBuilder indexBExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-b")).thenReturn(indexBExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureB = mock(ListenableActionFuture.class);
        when(indexBExistsRequestBuilder.execute()).thenReturn(listenableActionFutureB);
        IndicesExistsResponse indexBExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureB.actionGet()).thenReturn(indexBExistsResponse);
        when(indexBExistsResponse.exists()).thenReturn(true);

        String oldIndexName = underTest.resolveOldIndexName(indicesAdminClient, indexRootName);
        assertEquals(indexRootName + "-b", oldIndexName);
    }

    @Test
    public void resolveOlIndexNameShouldResolveToIndexA() {
        String indexRootName = "index";
        IndicesAdminClient indicesAdminClient = mock(IndicesAdminClient.class);
        IndicesExistsRequestBuilder indexAExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-a")).thenReturn(indexAExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureA = mock(ListenableActionFuture.class);
        when(indexAExistsRequestBuilder.execute()).thenReturn(listenableActionFutureA);
        IndicesExistsResponse indexAExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureA.actionGet()).thenReturn(indexAExistsResponse);
        when(indexAExistsResponse.exists()).thenReturn(true);

        IndicesExistsRequestBuilder indexBExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-b")).thenReturn(indexBExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureB = mock(ListenableActionFuture.class);
        when(indexBExistsRequestBuilder.execute()).thenReturn(listenableActionFutureB);
        IndicesExistsResponse indexBExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureB.actionGet()).thenReturn(indexBExistsResponse);
        when(indexBExistsResponse.exists()).thenReturn(false);

        String oldIndexName = underTest.resolveOldIndexName(indicesAdminClient, indexRootName);
        assertEquals(indexRootName + "-a", oldIndexName);
    }

    @Test
    public void executeShouldSucceed() {
        String indexRootName = "index";
        IndicesAdminClient indicesAdminClient = mock(IndicesAdminClient.class);
        Map<String, Object> indexConfig = Maps.newHashMap();
        Object settingsAsString = "{\"index.analysis.analyzer.default.filter.1\":\"lowercase\"}";
        indexConfig.put("settings", settingsAsString);
        IndicesExistsRequestBuilder indexAExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-a")).thenReturn(indexAExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureA = mock(ListenableActionFuture.class);
        when(indexAExistsRequestBuilder.execute()).thenReturn(listenableActionFutureA);
        IndicesExistsResponse indexAExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureA.actionGet()).thenReturn(indexAExistsResponse);
        when(indexAExistsResponse.exists()).thenReturn(true);

        IndicesExistsRequestBuilder indexBExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-b")).thenReturn(indexBExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureB = mock(ListenableActionFuture.class);
        when(indexBExistsRequestBuilder.execute()).thenReturn(listenableActionFutureB);
        IndicesExistsResponse indexBExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureB.actionGet()).thenReturn(indexBExistsResponse);
        when(indexBExistsResponse.exists()).thenReturn(false);

        when(indicesAdminClient.prepareCreate(indexRootName + "-b")).thenReturn()

        underTest.execute(indicesAdminClient, indexRootName, indexConfig);
    }
}
