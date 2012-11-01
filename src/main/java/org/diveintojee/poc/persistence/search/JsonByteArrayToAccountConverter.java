/**
 *
 */
package org.diveintojee.poc.persistence.search;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.diveintojee.poc.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author louis.gueye@gmail.com
 */
@Component(JsonByteArrayToAccountConverter.BEAN_ID)
public class JsonByteArrayToAccountConverter implements Converter<byte[], Account> {

    public static final String BEAN_ID = "jsonByteArrayToAccountConverter";

    @Autowired
    private ObjectMapper jsonMapper;

    /**
     * @see org.springframework.core.convert.converter.Converter#convert(Object)
     */
    @Override
    public Account convert(final byte[] source) {
        if (source == null || source.length == 0) return null;
        try {
            this.jsonMapper.getDeserializationConfig()
                    .without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
            // System.out.write(source);
            final Account account = this.jsonMapper.readValue(source, Account.class);
            return account;
        } catch (final IOException ignored) {
            throw new IllegalStateException(ignored);
        }

    }
}
