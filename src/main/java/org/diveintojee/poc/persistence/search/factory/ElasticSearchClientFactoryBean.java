package org.diveintojee.poc.persistence.search.factory;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Not scanned, included in *search.xml and *search-test.xml file<br/>
 * The first one uses 'remote' typology and the second one uses 'local' one<br/>
 *
 * @author louis.gueye@gmail.com
 */
public class ElasticSearchClientFactoryBean extends AbstractFactoryBean<Client> {

    private ElasticSearchClientTypology typology;

    private List<String> nodes;

    private Resource nodeConfigLocation;

    private IndicesUpdateStrategy indicesUpdateStrategy;

    private String configFormat;

    private Client client;

    private Node node;

    private DropCreateIndicesCommand dropCreateIndicesCommand;

    private MergeIndicesCommand mergeIndicesCommand;

    private FileHelper fileHelper;

    public static final String ELASTICSEARCH_CONFIGURATION_ROOT_FOLDER_NAME = "/elasticsearch";

    /**
     * Required
     * Supported values: {local|remote}
     * <p/>
     * <pre>
     *     {@code
     *     <property name="typology" value="remote"/>
     *     }
     * </pre>
     *
     * @see ElasticSearchClientTypology
     */
    public void setTypology(ElasticSearchClientTypology typology) {
        this.typology = typology;
    }

    /**
     * Required if remote typology is chosen
     * Supported node pattern: 'host:port' <br/>
     * Multiple node example:
     * <p/>
     * <pre>
     *   {@code
     *    <property name="nodes">
     *      <list>
     *        <value>jack:9300</value>
     *        <value>sparrow:9300</value>
     *        <value>bill:9300</value>
     *        <value>bottier:9300</value>
     *      </list>
     *    </property>
     *   }
     * </pre>
     */
    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    /**
     * Optional
     * Defaults to "classpath:elasticsearch/_settings.json"<br/>
     * <p/>
     * <pre>
     *     {@code
     *     <property name="nodeConfigLocation" value="classpath:/elasticsearch/_settings.json"/>
     *     }
     * </pre>
     * <p/>
     * That file is a suitable place to specify "cluster.name" property which defaults to "elasticsearch"
     *
     * @see <a href="http://www.elasticsearch.org/guide/reference/modules/discovery/">Elastisearch
     *      discovery module</a>
     */
    public void setNodeConfigLocation(Resource nodeConfigLocation) {
        this.nodeConfigLocation = nodeConfigLocation;
    }

    /**
     * Optional
     * Defaults to dropcreate
     * Supported values: {dropcreate|merge}
     *
     * @see IndicesUpdateStrategy
     */
    public void setIndicesUpdateStrategy(IndicesUpdateStrategy indicesUpdateStrategy) {
        this.indicesUpdateStrategy = indicesUpdateStrategy;
    }

    /**
     * Optional
     * Defaults to 'json'
     * Supported values: {json|yml}
     */
    public void setConfigFormat(String configFormat) {
        this.configFormat = configFormat;
    }

    public void setDropCreateIndicesCommand(DropCreateIndicesCommand dropCreateIndicesCommand) {
        this.dropCreateIndicesCommand = dropCreateIndicesCommand;
    }

    public void setFileHelper(FileHelper fileHelper) {
      this.fileHelper = fileHelper;
    }

  /**
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#getObjectType()
     */
    @Override
    public Class<Client> getObjectType() {
        return Client.class;
    }

    /**
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        
      if (Strings.isNullOrEmpty(configFormat))
          configFormat = "json";

      Map<String, Object> config = resolveElasticsearchConfig(configFormat);

      applyIndicesUpdateStrategy(config);
    }

  Map<String, Object> resolveElasticsearchConfig(String format) throws IOException {
    final HashMap<String,Object> config = Maps.newHashMap();
    File rootFolder = new ClassPathResource(ELASTICSEARCH_CONFIGURATION_ROOT_FOLDER_NAME).getFile();
    String nodeSettingsLocation = rootFolder.getAbsolutePath().concat(File.separator).concat("_settings.").concat(format);
    String settingsAsString = fileHelper.fileContentAsString(nodeSettingsLocation);
    System.out.println("resolved node settings = " + settingsAsString);
    config.put("settings", settingsAsString);
    Map<String, Object> indices = resolveIndicesConfig(rootFolder, format);
    config.put("indices", indices);

    return config;
  }

  Map<String, Object> resolveIndicesConfig(File rootFolder, String format)
      throws IOException {
    File[] folders = fileHelper.listChildrenDirectories(rootFolder);
    Map<String, Object> indices = Maps.newHashMap();
    if (ArrayUtils.isEmpty(folders)) {
      System.out.println("no children directory found under " + rootFolder);
      return indices;
    }
    // Iterating under /elasticsearch
    for (File folder : folders) {
      String indexPath = folder.getPath();
      String name = indexPath.substring(indexPath.lastIndexOf(File.separator) + 1, indexPath.length());
      System.out.println("resolved index name = " + name);
      String indexSettingsAsString = null;
      Collection<File> indexFiles = fileHelper.listFilesByExtension(folder, format);
      if (indexFiles.isEmpty()) {
        System.out.println("no document under " + indexPath + "found with extension " + format);
      }
      Iterator<File> indexFilesIterator = indexFiles.iterator();
      Map<String, Object> mappings = Maps.newHashMap();
      while (indexFilesIterator.hasNext()) {
          File file = indexFilesIterator.next();
          if (file.getAbsolutePath().contains("_settings.")) {
            String indexSettingsLocation = indexPath.concat(File.separator).concat("_settings.").concat(format);
            indexSettingsAsString = fileHelper.fileContentAsString(indexSettingsLocation);
            System.out.println("resolved index settings = " + indexSettingsAsString);
          } else {
            String mappingPath = file.getPath();
            String type = mappingPath.substring(mappingPath.lastIndexOf(File.separator) + 1, mappingPath.indexOf("." + format));
            System.out.println("resolved index type = " + type);
            final String mappingAsString = fileHelper.fileContentAsString(mappingPath);
            System.out.println("resolved mapping content = " + mappingAsString);
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

    void applyIndicesUpdateStrategy(Map<String, Object> config) throws IOException {

        if (indicesUpdateStrategy == null)
            indicesUpdateStrategy = IndicesUpdateStrategy.dropcreate;

        switch (indicesUpdateStrategy) {
            case dropcreate:
                dropCreateIndicesCommand.apply(client, config);
                break;
            case merge:
                mergeIndicesCommand.execute(client, configFormat);
                break;
        }
    }

    /**
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
     */
    @Override
    protected Client createInstance() throws Exception {

        if (typology == null)
            throw new BeanCreationException("Error creating " + Client.class.getName()
                    + ": 'typology' property is required. Between "
                    + ElasticSearchClientTypology.values());


        if (nodeConfigLocation == null)
            nodeConfigLocation = new ClassPathResource("/elasticsearch/_settings.json");


        switch (typology) {

            case local:
                NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder();
                nodeBuilder.settings().loadFromUrl(nodeConfigLocation.getURL());
                node = nodeBuilder.node();
                client = node.client();
                break;

            case remote:

                if (CollectionUtils.isEmpty(nodes)) {
                    throw new BeanCreationException("Error creating " + Client.class.getName()
                            + ": 'nodes' property is required if 'remote' typology is set");
                }
                Collection<InetSocketTransportAddress> addresses = fromNodes(nodes);
                ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder();
                builder.loadFromUrl(nodeConfigLocation.getURL());
                builder.put("client.transport.sniff", true);
                client = new TransportClient(builder.build());
                for (InetSocketTransportAddress address : addresses) {
                    ((TransportClient) client).addTransportAddress(address);
                }
                break;

        }

        return client;

    }

    /**
     * Assumes that nodes use <host>:<port> pattern
     */
    protected Collection<InetSocketTransportAddress> fromNodes(List<String> nodes) {
        return Collections2.transform(nodes, new Function<String, InetSocketTransportAddress>() {
            @Override
            public InetSocketTransportAddress apply(String node) {
                StringTokenizer tokenizer = new StringTokenizer(node, ":", false);
                String host = tokenizer.nextToken();
                String portAsString = tokenizer.nextToken();
                return new InetSocketTransportAddress(host, Integer.valueOf(portAsString));
            }
        });
    }

    /**
     * Destroy the singleton instance, if any.
     *
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        super.destroy();
        if (client != null) {
            client.close();
        }
        if (node != null) {
            node.close();
        }
    }

}
