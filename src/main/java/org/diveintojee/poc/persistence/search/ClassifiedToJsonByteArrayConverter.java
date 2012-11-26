/**
 *
 */
package org.diveintojee.poc.persistence.search;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.diveintojee.poc.domain.Classified;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author louis.gueye@gmail.com
 */
@Component(ClassifiedToJsonByteArrayConverter.BEAN_ID)
public class ClassifiedToJsonByteArrayConverter implements Converter<Classified, byte[]> {

    public static final String BEAN_ID = "classifiedToJsonByteArrayConverter";

    @Autowired
    private ObjectMapper jsonMapper;

    /**
     * @see org.springframework.core.convert.converter.Converter#convert(Object)
     */
    @Override
    public byte[] convert(final Classified source) {
        if (source == null) return null;
        this.jsonMapper.getSerializationConfig().without(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        String string;
        try {
            string = this.jsonMapper.writeValueAsString(source);
            return string.getBytes("utf-8");
        } catch (final Throwable th) {
            throw new IllegalArgumentException(th);
        }
    }
}
