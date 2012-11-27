package org.diveintojee.poc.persistence.search.factory;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author louis.gueye@gmail.com
 */
@Component
public class DropCreateIndexCommand {

    String resolveNewIndexName(String oldIndexName, final String indexRootName) {
        return oldIndexName.endsWith("-a") ? indexRootName + "-b" : indexRootName + "-a";
    }

    String resolveOldIndexName(IndicesAdminClient indicesAdminClient, String indexRootName) {
        String oldIndexName;
        final boolean indexAExists = indicesAdminClient.prepareExists(indexRootName + "-a").execute().actionGet().exists();
        final boolean indexBExists = indicesAdminClient.prepareExists(indexRootName + "-b").execute().actionGet().exists();
        if (indexAExists && indexBExists) {
            throw new IllegalStateException("Only 1 " + indexRootName + " index should exist at a time");
        } else if (!indexAExists && !indexBExists) {
            oldIndexName = indexRootName + "-b";
        } else if (indexAExists) {
            oldIndexName = indexRootName + "-a";
        } else {
            oldIndexName = indexRootName + "-b";
        }
        return oldIndexName;
    }

    public void execute(IndicesAdminClient indicesAdminClient, String indexRootName, Map<String, Object> index) {
        String oldIndexName = resolveOldIndexName(indicesAdminClient, indexRootName);
        String newIndexName = resolveNewIndexName(oldIndexName, indexRootName);
        String settings = (String) index.get("settings");
        CreateIndexResponse
                createIndexResponse =
                indicesAdminClient.prepareCreate(newIndexName).setSettings(settings).execute().actionGet();
        if (!createIndexResponse.acknowledged()) {
            throw new RuntimeException("Failed to create index '" + newIndexName + "'");
        }

        final Map<String, String> mappings = (Map<String, String>) index.get("mappings");

        for (String type : mappings.keySet()) {
            String mappingSource = mappings.get(type);
            PutMappingResponse
                    putMappingResponse =
                    indicesAdminClient.preparePutMapping(newIndexName).setSource(mappingSource).setType(
                            type)
                            .execute().actionGet();
            if (!putMappingResponse.acknowledged()) {
                throw new RuntimeException("Failed to put mapping '" + type + "' for index '" + newIndexName + "'");
            }

        }

        final
        IndicesAliasesResponse
                indicesAddAliasesResponse =
                indicesAdminClient.prepareAliases().addAlias(newIndexName, indexRootName)
                        .execute().actionGet();
        if (!indicesAddAliasesResponse.acknowledged()) {
            throw new RuntimeException("Failed to add index '" + newIndexName + "' to alias '" + indexRootName + "'");
        }

        final boolean oldIndexExists = indicesAdminClient.prepareExists(oldIndexName).execute().actionGet().exists();

        if (oldIndexExists) {
            final
            IndicesAliasesResponse
                    indicesAliasesRemoveResponse =
                    indicesAdminClient.prepareAliases().removeAlias(oldIndexName, indexRootName)
                            .execute().actionGet();

            if (!indicesAliasesRemoveResponse.acknowledged()) {
                throw new RuntimeException("Failed to remove index '" + oldIndexName + "' from alias '" + indexRootName + "'");
            }

            DeleteIndexResponse
                    deleteIndexResponse =
                    indicesAdminClient.prepareDelete(oldIndexName).execute().actionGet();
            if (!deleteIndexResponse.acknowledged()) {
                throw new RuntimeException("Failed to delete index '" + oldIndexName + "'");
            }
        }

        index.put("write-index", newIndexName);
    }
}
