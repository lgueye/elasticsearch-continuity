package org.diveintojee.poc.persistence.search.factory;

import com.google.common.collect.Maps;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author louis.gueye@gmail.com
 */
@Component
public class ElasticSearchConfigResolver {

    @Autowired
    private FileHelper fileHelper;

    public static final String ELASTICSEARCH_CONFIGURATION_ROOT_FOLDER_NAME = "/elasticsearch";

    public Map<String, Object> resolveElasticsearchConfig(String format) throws IOException {
        final HashMap<String, Object> config = Maps.newHashMap();
        File rootFolder = new ClassPathResource(ELASTICSEARCH_CONFIGURATION_ROOT_FOLDER_NAME).getFile();
        String nodeSettingsLocation = ELASTICSEARCH_CONFIGURATION_ROOT_FOLDER_NAME.concat(
            File.separator).concat("_settings.").concat(format);
        String settingsAsString = fileHelper.fileContentAsString(nodeSettingsLocation);
//        System.out.println("resolved node settings = " + settingsAsString);
        config.put("settings", settingsAsString);
        Map<String, Object> indices = resolveIndicesConfig(rootFolder, format);
        config.putAll(indices);

        return config;
    }

    Map<String, Object> resolveIndicesConfig(File rootFolder, String format)
            throws IOException {
        File[] folders = fileHelper.listChildrenDirectories(rootFolder);
        Map<String, Object> indices = Maps.newHashMap();
      final String rootFolderAbsolutePath = rootFolder.getAbsolutePath();
      String classpathElasticSearchRootPath = rootFolderAbsolutePath
          .substring(rootFolderAbsolutePath.lastIndexOf(File.separator), rootFolderAbsolutePath.length());
        if (ArrayUtils.isEmpty(folders)) {
//            System.out.println("no children directory found under " + rootFolder);
            return indices;
        }
        // Iterating under /elasticsearch
        for (File folder : folders) {
            String indexPath = folder.getPath();
            String name = indexPath.substring(indexPath.lastIndexOf(File.separator) + 1, indexPath.length());
//            System.out.println("resolved index name = " + name);
            final
            String
                indexRelativePath =
                classpathElasticSearchRootPath.concat(File.separator)
                    .concat(name);

            String indexSettingsAsString = null;
            Collection<File> indexFiles = fileHelper.listFilesByExtension(folder, format);
//            if (indexFiles.isEmpty()) {
//                System.out.println("no document under " + indexPath + " found with extension " + format);
//            }
            Iterator<File> indexFilesIterator = indexFiles.iterator();
            Map<String, Object> mappings = Maps.newHashMap();
            while (indexFilesIterator.hasNext()) {
                File file = indexFilesIterator.next();
                if (file.getAbsolutePath().contains("_settings.")) {
                    String indexSettingsLocation = indexRelativePath.concat(File.separator).concat("_settings.").concat(format);
                    indexSettingsAsString = fileHelper.fileContentAsString(indexSettingsLocation);
//                    System.out.println("resolved index settings = " + indexSettingsAsString);
                } else {
                    String mappingPath = file.getPath();
                    String type = mappingPath.substring(mappingPath.lastIndexOf(File.separator) + 1, mappingPath.indexOf("." + format));
                    System.out.println("resolved index type = " + type);
                    String mappingRelativePath = indexRelativePath.concat(File.separator).concat(type).concat(".").concat(format);
                    final String mappingAsString = fileHelper.fileContentAsString(mappingRelativePath);
//                    System.out.println("resolved mapping content = " + mappingAsString);
                    mappings.put(type, mappingAsString);
                }
            }

            Map<String, Object> index = Maps.newHashMap();
            index.put("settings", indexSettingsAsString);
            index.put("mappings", mappings);
            indices.put(name, index);

        }

        return indices;
    }

}
