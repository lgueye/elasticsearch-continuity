/**
 *
 */
package org.diveintojee.poc.persistence.search;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.diveintojee.poc.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author louis.gueye@gmail.com
 */
@Component(AccountToJsonByteArrayConverter.BEAN_ID)
public class AccountToJsonByteArrayConverter implements Converter<Account, byte[]> {

    public static final String BEAN_ID = "accountToJsonByteArrayConverter";

    @Autowired
    private ObjectMapper jsonMapper;

    /**
     * @see org.springframework.core.convert.converter.Converter#convert(Object)
     */
    @Override
    public byte[] convert(final Account source) {
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
