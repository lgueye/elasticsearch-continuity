package org.diveintojee.poc.persistence.search;

import com.google.common.collect.Lists;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
@Component
public class DropCreateIndicesCommand {

    public static final String ELASTICSEARCH_CONFIGURATION_ROOT_FOLDER_NAME = "/elasticsearch";

    @Autowired
    private FileHelper fileHelper;

    public DropCreateIndicesCommand() {
    }

    public void execute(final Client client, String configFormat) throws IOException {
        File rootFolder = new ClassPathResource(ELASTICSEARCH_CONFIGURATION_ROOT_FOLDER_NAME).getFile();
        List<IndexConfiguration> indicesConfiguration = scanIndexConfigurations(rootFolder, configFormat);
        for (IndexConfiguration indexConfiguration : indicesConfiguration) {
            dropIndex(client, indexConfiguration);
            createIndex(client, indexConfiguration);
            putMappings(client, indexConfiguration);
        }
    }

    void putMappings(Client client, IndexConfiguration indexConfiguration) throws IOException {
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

    void createIndex(Client client, IndexConfiguration indexConfiguration) throws IOException {
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

    void dropIndex(Client client, IndexConfiguration indexConfiguration) {
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

    List<IndexConfiguration> scanIndexConfigurations(File rootFolder, String configFormat) throws IOException {
        File[] folders = fileHelper.listChildrenDirectories(rootFolder);
        List<IndexConfiguration> indexConfigurations = Lists.newArrayList();
        String rootFolderName = rootFolder.getName();
        for (File folder : folders) {
            IndexConfiguration indexConfiguration = newIndexConfiguration(rootFolderName, folder, configFormat);
            indexConfigurations.add(indexConfiguration);
        }

        return indexConfigurations;
    }

    IndexConfiguration newIndexConfiguration(String rootFolderName, File indexDirectory, String configFormat) {
        IndexConfiguration indexConfiguration = new IndexConfiguration();
        String folderPath = indexDirectory.getPath();
        String name = folderPath.substring(folderPath.lastIndexOf(File.separator) + 1, folderPath.length());
        indexConfiguration.setName(name);
        String configLocation = folderPath + File.separator + "_settings." + configFormat;
        String configPath = configLocation.substring(configLocation.indexOf(File.separator + rootFolderName), configLocation.length());
        indexConfiguration.setConfigLocation(configPath);
        List<MappingConfiguration> mappingConfigurations = mappingConfigurations(rootFolderName, indexDirectory, configFormat);
        indexConfiguration.setMappingConfigurations(mappingConfigurations);
        return indexConfiguration;
    }

    List<MappingConfiguration> mappingConfigurations(String rootFolderName, File indexDirectory, String configFormat) {
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

    MappingConfiguration newMappingConfiguration(String rootFolderName, File mapping, String format) {
        MappingConfiguration mappingConfiguration = new MappingConfiguration();
        String mappingPath = mapping.getPath();
        String type = mappingPath.substring(mappingPath.lastIndexOf(File.separator) + 1, mappingPath.indexOf("." + format));
        mappingConfiguration.setType(type);
        String location = mappingPath.substring(mappingPath.indexOf(File.separator + rootFolderName), mappingPath.length());
        mappingConfiguration.setLocation(location);
        return mappingConfiguration;
    }


}
