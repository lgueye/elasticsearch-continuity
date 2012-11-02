/*
 *
 */
package org.diveintojee.poc;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.diveintojee.poc.domain.Classified;
import org.joda.time.DateTime;
import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * @author louis.gueye@gmail.com
 */
public abstract class TestFixtures {

    public static final String STANDARD_CHARSET = "azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBNéèçàù7894561230";
    private static final String EMAIL_CHARSET = "azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN";
    private static final String baseEndPoint = ResourceBundle.getBundle("stories-context").getString(
            "baseEndPoint");

    /**
     * @param constraintViolationException
     * @param expectedMessage
     * @param expectedPath
     */
    public static void assertViolationContainsTemplateAndMessage(
            final ConstraintViolationException constraintViolationException, final String expectedMessage,
            final String expectedPath) {
        final Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
        Assert.assertNotNull(constraintViolations);
        Assert.assertEquals(1, CollectionUtils.size(constraintViolations));
        // for (final ConstraintViolation<?> constraintViolation :
        // constraintViolations) {
        // System.out.println("---------------------------------------->" +
        // constraintViolation.getMessage());
        //
        // }
        final ConstraintViolation<?> violation = constraintViolations.iterator().next();
        Assert.assertEquals(expectedMessage, violation.getMessage());
        Assert.assertEquals(expectedPath, violation.getPropertyPath().toString());
    }

    /**
     * @param e
     * @param errorCode
     * @param propertyPath
     */
    public static void assertViolationContainsTemplateAndPath(final ConstraintViolationException e,
                                                              final String errorCode, final String propertyPath) {
        Assert.assertNotNull(e.getConstraintViolations());
        Assert.assertEquals(1, CollectionUtils.size(e.getConstraintViolations()));
        final ConstraintViolation<?> violation = e.getConstraintViolations().iterator().next();
        Assert.assertEquals(errorCode, violation.getMessageTemplate());
        Assert.assertEquals(propertyPath, violation.getPropertyPath().toString());
    }


    public static <T> T fromJson(final String json, final Class<T> clazz) throws JsonParseException,
            JsonMappingException, IOException {

        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json.getBytes(), clazz);

    }

    @SuppressWarnings("unchecked")
    public static <T> T fromXml(final String xml, final Class<T> clazz) throws JAXBException,
            UnsupportedEncodingException {

        final JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{clazz});

        final Unmarshaller xmlUnmarshaller = jaxbContext.createUnmarshaller();

        return (T) xmlUnmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes("UTF-8")));

    }

    public static <T> String toJson(final T object) throws JsonGenerationException, JsonMappingException, IOException {

        final ObjectMapper mapper = new ObjectMapper();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        mapper.writeValue(out, object);

        return out.toString();

    }

    public static <T> String toXml(final T object) throws JAXBException, UnsupportedEncodingException {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        final JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{object.getClass()});

        final Marshaller xmlMarshaller = jaxbContext.createMarshaller();

        xmlMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        xmlMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        xmlMarshaller.marshal(object, out);

        return out.toString("UTF-8");

    }

    /**
     * @return
     */
    public static Classified validClassified() {
        final Classified classified = new Classified();
        classified.setCreated(new DateTime());
        classified.setTitle(RandomStringUtils.random(Classified.CONSTRAINT_TITLE_MAX_SIZE,
                                                     TestFixtures.STANDARD_CHARSET));
        classified.setDescription(
            RandomStringUtils.random(Classified.CONSTRAINT_DESCRIPTION_MAX_SIZE,
                                     TestFixtures.STANDARD_CHARSET));
        return classified;
    }
}
