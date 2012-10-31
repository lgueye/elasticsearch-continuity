/**
 *
 */
package org.diveintojee.poc.persistence.search;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import fr.midipascher.domain.Restaurant;

/**
 * @author louis.gueye@gmail.com
 */
@Component(RestaurantToJsonByteArrayConverter.BEAN_ID)
public class RestaurantToJsonByteArrayConverter implements Converter<Restaurant, byte[]> {

    public static final String BEAN_ID = "restaurantToJsonByteArrayConverter";

    @Autowired
    private ObjectMapper jsonMapper;

    /**
     * @see org.springframework.core.convert.converter.Converter#convert(Object)
     */
    @Override
    public byte[] convert(final Restaurant source) {
        if (source == null) return null;
        this.jsonMapper.getSerializationConfig().without(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        String string;
        try {
            string = this.jsonMapper.writeValueAsString(source);
            return string.getBytes("utf-8");
        } catch (final Throwable th) {
            throw new IllegalArgumentException(th);
        }
        // System.out.println("source as string = " + string);
    }
}
