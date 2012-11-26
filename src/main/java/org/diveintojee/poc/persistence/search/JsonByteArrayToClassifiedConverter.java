/**
 *
 */
package org.diveintojee.poc.persistence.search;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.diveintojee.poc.domain.Classified;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author louis.gueye@gmail.com
 */
@Component(JsonByteArrayToClassifiedConverter.BEAN_ID)
public class JsonByteArrayToClassifiedConverter implements Converter<byte[], Classified> {

    public static final String BEAN_ID = "jsonByteArrayToClassifiedConverter";

    @Autowired
    private ObjectMapper jsonMapper;

    /**
     * @see org.springframework.core.convert.converter.Converter#convert(Object)
     */
    @Override
    public Classified convert(final byte[] source) {
        if (source == null || source.length == 0) return null;
        try {
            this.jsonMapper.getDeserializationConfig()
                    .without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
            final Classified classified = this.jsonMapper.readValue(source, Classified.class);
            return classified;
        } catch (final IOException ignored) {
            throw new IllegalStateException(ignored);
        }

    }
}
