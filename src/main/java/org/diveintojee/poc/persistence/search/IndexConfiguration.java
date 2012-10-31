package org.diveintojee.poc.persistence.search;

import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
public class IndexConfiguration {

    private String name;
    private String configLocation;
    private List<MappingConfiguration> mappingConfigurations;

    public String getName() {
        return name;
    }


    public String getConfigLocation() {
        return configLocation;
    }

    public List<MappingConfiguration> getMappingConfigurations() {
        return mappingConfigurations;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }


    public void setMappingConfigurations(List<MappingConfiguration> mappingConfigurations) {
        this.mappingConfigurations = mappingConfigurations;
    }
}
