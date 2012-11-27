package org.diveintojee.poc.persistence.search.factory;

import com.google.common.collect.Maps;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        final String newIndexName = indexRootName + "-b";
        final String oldIndexName = indexRootName + "-a";
        IndicesAdminClient indicesAdminClient = mock(IndicesAdminClient.class);
        Map<String, Object> indexConfig = Maps.newHashMap();
        String settingsAsString = "{\"index.analysis.analyzer.default.filter.1\":\"lowercase\"}";
        indexConfig.put("settings", settingsAsString);
        Map<String, String> mappings = Maps.newHashMap();
        String type1 = "type1";
        String mapping1 = "mapping1";
        mappings.put(type1, mapping1);
        indexConfig.put("mappings", mappings);
        IndicesExistsRequestBuilder indexAExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(indexRootName + "-a")).thenReturn(indexAExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureA = mock(ListenableActionFuture.class);
        when(indexAExistsRequestBuilder.execute()).thenReturn(listenableActionFutureA);
        IndicesExistsResponse indexAExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureA.actionGet()).thenReturn(indexAExistsResponse);
        when(indexAExistsResponse.exists()).thenReturn(true);

        IndicesExistsRequestBuilder indexBExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(newIndexName)).thenReturn(indexBExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> listenableActionFutureB = mock(ListenableActionFuture.class);
        when(indexBExistsRequestBuilder.execute()).thenReturn(listenableActionFutureB);
        IndicesExistsResponse indexBExistsResponse = mock(IndicesExistsResponse.class);
        when(listenableActionFutureB.actionGet()).thenReturn(indexBExistsResponse);
        when(indexBExistsResponse.exists()).thenReturn(false);

        CreateIndexRequestBuilder createIndexRequestBuilder = mock(CreateIndexRequestBuilder.class);
        when(indicesAdminClient.prepareCreate(newIndexName)).thenReturn(createIndexRequestBuilder);
        when(createIndexRequestBuilder.setSettings(settingsAsString)).thenReturn(createIndexRequestBuilder);
        ListenableActionFuture<CreateIndexResponse> createIndexListenableActionFuture = mock(ListenableActionFuture.class);
        when(createIndexRequestBuilder.execute()).thenReturn(createIndexListenableActionFuture);
        CreateIndexResponse createIndexResponse = mock(CreateIndexResponse.class);
        when(createIndexListenableActionFuture.actionGet()).thenReturn(createIndexResponse);
        when(createIndexResponse.acknowledged()).thenReturn(true);

        PutMappingRequestBuilder putMappingRequestBuilder = mock(PutMappingRequestBuilder.class);
        when(indicesAdminClient.preparePutMapping(newIndexName)).thenReturn(putMappingRequestBuilder);
        when(putMappingRequestBuilder.setSource(mapping1)).thenReturn(putMappingRequestBuilder);
        when(putMappingRequestBuilder.setType(type1)).thenReturn(putMappingRequestBuilder);
        ListenableActionFuture<PutMappingResponse> putMappingListenableActionFuture = mock(ListenableActionFuture.class);
        when(putMappingRequestBuilder.execute()).thenReturn(putMappingListenableActionFuture);
        PutMappingResponse putMappingResponse = mock(PutMappingResponse.class);
        when(putMappingListenableActionFuture.actionGet()).thenReturn(putMappingResponse);
        when(putMappingResponse.acknowledged()).thenReturn(true);

        IndicesAliasesRequestBuilder indicesAliasesRequestBuilder = mock(IndicesAliasesRequestBuilder.class);
        when(indicesAdminClient.prepareAliases()).thenReturn(indicesAliasesRequestBuilder);

        IndicesAliasesRequestBuilder indicesAddAliasesRequestBuilder = mock(IndicesAliasesRequestBuilder.class);
        when(indicesAliasesRequestBuilder.addAlias(newIndexName, indexRootName)).thenReturn(indicesAddAliasesRequestBuilder);
        ListenableActionFuture<IndicesAliasesResponse> addAliaslistenableActionFuture = mock(ListenableActionFuture.class);
        when(indicesAddAliasesRequestBuilder.execute()).thenReturn(addAliaslistenableActionFuture);
        IndicesAliasesResponse addAliasIndicesAliasesResponse = mock(IndicesAliasesResponse.class);
        when(addAliaslistenableActionFuture.actionGet()).thenReturn(addAliasIndicesAliasesResponse);
        when(addAliasIndicesAliasesResponse.acknowledged()).thenReturn(true);

        IndicesExistsRequestBuilder indicesExistsRequestBuilder = mock(IndicesExistsRequestBuilder.class);
        when(indicesAdminClient.prepareExists(oldIndexName)).thenReturn(indicesExistsRequestBuilder);
        ListenableActionFuture<IndicesExistsResponse> indicesExistsListenableActionFuture = mock(ListenableActionFuture.class);
        when(indicesExistsRequestBuilder.execute()).thenReturn(indicesExistsListenableActionFuture);
        IndicesExistsResponse indicesExistsResponse = mock(IndicesExistsResponse.class);
        when(indicesExistsListenableActionFuture.actionGet()).thenReturn(indicesExistsResponse);
        when(indicesExistsResponse.exists()).thenReturn(true);

        IndicesAliasesRequestBuilder indicesRemoveAliasesRequestBuilder = mock(IndicesAliasesRequestBuilder.class);
        when(indicesAliasesRequestBuilder.removeAlias(oldIndexName, indexRootName)).thenReturn(indicesRemoveAliasesRequestBuilder);
        ListenableActionFuture<IndicesAliasesResponse> removeAliaslistenableActionFuture = mock(ListenableActionFuture.class);
        when(indicesRemoveAliasesRequestBuilder.execute()).thenReturn(removeAliaslistenableActionFuture);
        IndicesAliasesResponse removeAliasIndicesAliasesResponse = mock(IndicesAliasesResponse.class);
        when(removeAliaslistenableActionFuture.actionGet()).thenReturn(removeAliasIndicesAliasesResponse);
        when(removeAliasIndicesAliasesResponse.acknowledged()).thenReturn(true);

        DeleteIndexRequestBuilder deleteIndexRequestBuilder = mock(DeleteIndexRequestBuilder.class);
        when(indicesAdminClient.prepareDelete(oldIndexName)).thenReturn(deleteIndexRequestBuilder);
        ListenableActionFuture<DeleteIndexResponse> deleteIndexListenableActionFuture = mock(ListenableActionFuture.class);
        when(deleteIndexRequestBuilder.execute()).thenReturn(deleteIndexListenableActionFuture);
        DeleteIndexResponse deleteIndexResponse = mock(DeleteIndexResponse.class);
        when(deleteIndexListenableActionFuture.actionGet()).thenReturn(deleteIndexResponse);
        when(deleteIndexResponse.acknowledged()).thenReturn(true);

        underTest.execute(indicesAdminClient, indexRootName, indexConfig);

        verify(createIndexResponse).acknowledged();
        verify(putMappingResponse).acknowledged();
        verify(addAliasIndicesAliasesResponse).acknowledged();
        verify(indicesExistsResponse, times(2)).exists();
        verify(removeAliasIndicesAliasesResponse).acknowledged();
        verify(deleteIndexResponse).acknowledged();

        assertEquals(newIndexName, indexConfig.get("write-index"));
    }

}
