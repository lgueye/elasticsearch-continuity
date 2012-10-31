/**
 *
 */
package org.diveintojee.poc.persistence.search;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

import fr.midipascher.domain.Restaurant;

/**
 * @author louis.gueye@gmail.com
 */
@Component(JsonByteArrayToRestaurantConverter.BEAN_ID)
public class JsonByteArrayToRestaurantConverter implements Converter<byte[], Restaurant> {

    public static final String BEAN_ID = "jsonByteArrayToRestaurantConverter";

    @Autowired
    private ObjectMapper jsonMapper;

    /**
     * @see org.springframework.core.convert.converter.Converter#convert(Object)
     */
    @Override
    public Restaurant convert(final byte[] source) {
        if (source == null || source.length == 0) return null;
        try {
            this.jsonMapper.getDeserializationConfig()
                    .without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
            // System.out.write(source);
            final Restaurant restaurant = this.jsonMapper.readValue(source, Restaurant.class);
            return restaurant;
        } catch (final IOException ignored) {
            throw new IllegalStateException(ignored);
        }

    }
}
