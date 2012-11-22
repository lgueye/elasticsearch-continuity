/**
 *
 */
package org.diveintojee.poc.persistence.search;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.diveintojee.poc.domain.Classified;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassifiedToJsonByteArrayConverterTest {

    @Mock
    ObjectMapper jsonMapper;

    @InjectMocks
    ClassifiedToJsonByteArrayConverter underTest = new ClassifiedToJsonByteArrayConverter();

    /**
     */
    @Test
    public final void convertShoulReturnNullWithNullInput() {

        // Variables
        Classified source;

        // Given
        source = null;

        // When
        byte[] jsonByteArray = this.underTest.convert(source);

        // Then
        assertNull(jsonByteArray);
    }

    /**
     * Test method for
     * .
     *
     * @throws java.io.IOException
     * @throws org.codehaus.jackson.map.JsonMappingException
     *
     * @throws org.codehaus.jackson.JsonGenerationException
     *
     */
    @Test
    public final void convertShouldSucceed() throws JsonGenerationException, JsonMappingException, IOException {

        // Variables
        Classified source;
        SerializationConfig serializationConfig;
        String jsonString;
        byte[] jsonByteArray;

        // Given
        source = new Classified();
        serializationConfig = Mockito.mock(SerializationConfig.class);
        jsonString = "{}";
        jsonByteArray = jsonString.getBytes("utf-8");

        // When
        Mockito.when(this.jsonMapper.getSerializationConfig()).thenReturn(serializationConfig);
        Mockito.when(serializationConfig.without(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS)).thenReturn(
                serializationConfig);
        Mockito.when(this.jsonMapper.writeValueAsString(source)).thenReturn(jsonString);
        byte[] result = this.underTest.convert(source);

        // Then
        Mockito.verify(this.jsonMapper).getSerializationConfig();
        Mockito.verify(serializationConfig).without(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        Mockito.verify(this.jsonMapper).writeValueAsString(source);

        Mockito.verifyNoMoreInteractions(this.jsonMapper, serializationConfig);

        assertTrue(Arrays.equals(jsonByteArray, result));

    }
}
