/**
 *
 */
package org.diveintojee.poc.persistence.search;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.diveintojee.poc.domain.Classified;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonByteArrayToClassifiedConverterTest {

    @Mock
    ObjectMapper jsonMapper;

    @InjectMocks
    JsonByteArrayToClassifiedConverter underTest = new JsonByteArrayToClassifiedConverter();

    /**
     * Test method for
     * {@link org.diveintojee.poc.persistence.search.JsonByteArrayToClassifiedConverter#convert(byte[])}
     * .
     */
    @Test
    public final void convertShoulReturnNullWithNullInput() {

        // Variables
        byte[] source;
        Classified result;

        // Given
        source = null;

        // When
        result = this.underTest.convert(source);

        // Then
        assertNull(result);

        // Given
        source = new byte[]{};

        // When
        result = this.underTest.convert(source);

        // Then
        assertNull(result);

    }

    /**
     * Test method for
     * {@link org.diveintojee.poc.persistence.search.JsonByteArrayToClassifiedConverter#convert(byte[])}
     * .
     *
     * @throws java.io.IOException
     * @throws org.codehaus.jackson.map.JsonMappingException
     * @throws org.codehaus.jackson.JsonParseException
     */
    @Test
    public final void convertShouldSucceed() throws JsonParseException, JsonMappingException, IOException {

        // Variables
        byte[] source;
        Classified result;
        DeserializationConfig deserializationConfig;
        Classified fromJson;

        // Given
        source = "{}".getBytes("utf-8");
        deserializationConfig = Mockito.mock(DeserializationConfig.class);
        fromJson = Mockito.mock(Classified.class);

        // When
        Mockito.when(this.jsonMapper.getDeserializationConfig()).thenReturn(deserializationConfig);
        Mockito.when(deserializationConfig.without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES))
                .thenReturn(deserializationConfig);
        Mockito.when(this.jsonMapper.readValue(source, Classified.class)).thenReturn(fromJson);
        result = this.underTest.convert(source);

        // Then
        Mockito.verify(this.jsonMapper).getDeserializationConfig();
        Mockito.verify(deserializationConfig).without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        Mockito.verify(this.jsonMapper).readValue(source, Classified.class);

        Mockito.verifyNoMoreInteractions(this.jsonMapper, deserializationConfig);

        assertSame(fromJson, result);

    }

}
