package org.diveintojee.poc.persistence.search.factory;

import com.google.common.collect.Lists;

import org.diveintojee.poc.persistence.search.SearchIndices;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author louis.gueye@gmail.com
 */
@Component
public class DropCreateIndicesCommand {


//    @Autowired
//    private FileHelper fileHelper;

    public void execute(final Client client, Map<String, Object> config) throws IOException {
        File rootFolder = new ClassPathResource(ELASTICSEARCH_CONFIGURATION_ROOT_FOLDER_NAME).getFile();
        List<IndexConfiguration> indicesConfiguration = scanIndexConfigurations(rootFolder, configFormat);
        for (IndexConfiguration indexConfiguration : indicesConfiguration) {
            dropIndex(client, indexConfiguration);
            createIndex(client, indexConfiguration);
            putMappings(client, indexConfiguration);
            client.admin().indices().prepareAliases().addAlias(indexConfiguration.getName(),
                    SearchIndices.classifieds.name());
        }
    }

    public void putMappings(Client client, IndexConfiguration indexConfiguration) throws IOException {
        String indexName = indexConfiguration.getName();
        List<MappingConfiguration> mappingsConfiguration = indexConfiguration.getMappingConfigurations();
        for (MappingConfiguration mappingConfiguration : mappingsConfiguration) {
            String type = mappingConfiguration.getType();
            String mappingLocation = mappingConfiguration.getLocation();
            String mappingSource = fileHelper.fileContentAsString(mappingLocation);
            PutMappingResponse
                    putMappingResponse =
                    client.admin().indices().preparePutMapping(indexName).setSource(mappingSource).setType(type)
                            .execute().actionGet();
            if (!putMappingResponse.acknowledged()) {
                throw new RuntimeException(
                        "Failed to put mapping '" + type + "' for index '" + indexName + "'");
            }
        }
    }

    public void createIndex(Client client, IndexConfiguration indexConfiguration) throws IOException {
        String indexName = indexConfiguration.getName();
        String indexConfigLocation = indexConfiguration.getConfigLocation();
        String settings = fileHelper.fileContentAsString(indexConfigLocation);
        CreateIndexResponse
                createIndexResponse =
                client.admin().indices().prepareCreate(indexName).setSettings(settings).execute().actionGet();
        if (!createIndexResponse.acknowledged()) {
            throw new RuntimeException("Failed to create index '" + indexName + "'");
        }
    }

    public void dropIndex(Client client, IndexConfiguration indexConfiguration) {
        String indexName = indexConfiguration.getName();
        if (client.admin().indices().prepareExists(indexName).execute().actionGet().exists()) {
            DeleteIndexResponse
                    deleteIndexResponse =
                    client.admin().indices().prepareDelete(indexName).execute().actionGet();
            if (!deleteIndexResponse.acknowledged()) {
                throw new RuntimeException("Failed to delete index '" + indexName + "'");
            }
        }
    }

    public List<IndexConfiguration> scanIndexConfigurations(File rootFolder, String configFormat) throws IOException {
        File[] folders = fileHelper.listChildrenDirectories(rootFolder);
        List<IndexConfiguration> indexConfigurations = Lists.newArrayList();
        String rootFolderName = rootFolder.getName();
        for (File folder : folders) {
            IndexConfiguration indexConfiguration = newIndexConfiguration(rootFolderName, folder, configFormat);
            indexConfigurations.add(indexConfiguration);
        }

        return indexConfigurations;
    }

    public IndexConfiguration newIndexConfiguration(String rootFolderName, File indexDirectory,
                                                    String configFormat) {
        IndexConfiguration indexConfiguration = new IndexConfiguration();
        String folderPath = indexDirectory.getPath();
        String name = folderPath.substring(folderPath.lastIndexOf(File.separator) + 1, folderPath.length());
        indexConfiguration.setName(name);
        String configLocation = folderPath + File.separator + "_settings." + configFormat;
        String configPath = configLocation.substring(configLocation.lastIndexOf(File.separator + rootFolderName), configLocation.length());
        indexConfiguration.setConfigLocation(configPath);
        List<MappingConfiguration> mappingConfigurations = mappingConfigurations(rootFolderName, indexDirectory, configFormat);
        indexConfiguration.setMappingConfigurations(mappingConfigurations);
        return indexConfiguration;
    }

    public List<MappingConfiguration> mappingConfigurations(String rootFolderName,
                                                            File indexDirectory,
                                                            String configFormat) {
        Collection<File> indexFiles = fileHelper.listFilesByExtension(indexDirectory, configFormat);
        Iterator<File> indexFilesIterator = indexFiles.iterator();
        while (indexFilesIterator.hasNext()) {
            File file = indexFilesIterator.next();
            if (file.getAbsolutePath().contains("_settings")) indexFilesIterator.remove();
        }
        List<MappingConfiguration> mappingConfigurations = Lists.newArrayList();
        for (File mapping : indexFiles) {
            MappingConfiguration mappingConfiguration = newMappingConfiguration(rootFolderName, mapping, configFormat);
            mappingConfigurations.add(mappingConfiguration);
        }
        return mappingConfigurations;
    }

    public MappingConfiguration newMappingConfiguration(String rootFolderName, File mapping,
                                                        String format) {
        MappingConfiguration mappingConfiguration = new MappingConfiguration();
        String mappingPath = mapping.getPath();
        String type = mappingPath.substring(mappingPath.lastIndexOf(File.separator) + 1, mappingPath.indexOf("." + format));
        mappingConfiguration.setType(type);
        String location = mappingPath.substring(mappingPath.lastIndexOf(File.separator + rootFolderName), mappingPath.length());
        mappingConfiguration.setLocation(location);
        return mappingConfiguration;
    }

  private String resolveNewIndexName(String oldIndexName, final String indexRootName) {
      return oldIndexName.endsWith("-a") ? indexRootName + "-b" : indexRootName + "-a";
  }

  private String resolveOldIndexName(IndicesAdminClient indicesAdminClient, String indexRootName) {
      String oldIndexName;
    final boolean classifiedsAExists = indicesAdminClient.prepareExists(indexRootName + "-a").execute().actionGet().exists();
      final boolean classifiedsBExists = indicesAdminClient.prepareExists(indexRootName + "-b").execute().actionGet().exists();
      if (classifiedsAExists && classifiedsBExists) {
          throw new IllegalStateException("Only 1 " + indexRootName + " index should exist at a time");
      } else if (!classifiedsAExists && !classifiedsBExists) {
          oldIndexName = indexRootName + "-a";
      } else if (classifiedsAExists) {
          oldIndexName = indexRootName + "-b";
      } else {
          oldIndexName = indexRootName + "-a";
      }
      return oldIndexName;
  }


  public void apply(final Client elasticsearch, final Map<String, Object> config) throws IOException {
    final Map<String, Object> indices = (Map<String, Object>) config.get("indices");
    final IndicesAdminClient indicesAdminClient = elasticsearch.admin().indices();

    for (String indexRootName : indices.keySet()) {
      Map<String, Object> index = (Map<String, Object>) indices.get(indexRootName);
      String oldIndexName = resolveOldIndexName(indicesAdminClient, indexRootName);
      String newIndexName = resolveNewIndexName(oldIndexName, "classifieds");
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
            throw new RuntimeException(
                    "Failed to put mapping '" + type + "' for index '" + newIndexName + "'");
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

      final
      IndicesAliasesResponse
          indicesAliasesRemoveResponse =
          indicesAdminClient.prepareAliases().removeAlias(oldIndexName, indexRootName)
              .execute().actionGet();

      if (!indicesAddAliasesResponse.acknowledged()) {
        throw new RuntimeException("Failed to remove index '" + oldIndexName + "' from alias '" + indexRootName + "'");
      }

      if (indicesAdminClient.prepareExists(oldIndexName).execute().actionGet().exists()) {
          DeleteIndexResponse
                  deleteIndexResponse =
                  indicesAdminClient.prepareDelete(oldIndexName).execute().actionGet();
          if (!deleteIndexResponse.acknowledged()) {
              throw new RuntimeException("Failed to delete index '" + oldIndexName + "'");
          }
      }
      
    }
    
  }
}
