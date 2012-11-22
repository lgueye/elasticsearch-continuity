/*
 *
 */
package org.diveintojee.poc.business;

import org.apache.commons.lang3.RandomStringUtils;
import org.diveintojee.poc.TestFixtures;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.validation.ValidationContext;
import org.junit.Test;

import java.util.Locale;

/**
 * {@link Classified} validation testing<br/>
 * CRUD operations are tested<br>
 *
 * @author louis.gueye@gmail.com
 */
public class ClassifiedValidationTestIT extends BaseValidations {

    private Classified underTest = null;

    /**
     * Given : a valid cla valued with an invalid name<br/>
     * When : one persists the above classified<br/>
     * Then : system should throw a {@link javax.validation.ConstraintViolationException}<br/>
     */
    private void shouldValidateTitleRequiredConstraint(final ValidationContext context) {
        // Given
        String wrongData;
        underTest = TestFixtures.validClassified();

        wrongData = null;
        underTest.setTitle(wrongData);

        assertExpectedViolation(underTest, context, Locale.ENGLISH, "Title is required", "title");
        assertExpectedViolation(underTest, context, Locale.FRENCH, "Le titre est requis", "title");

        wrongData = "";
        underTest.setTitle(wrongData);

        assertExpectedViolation(underTest, context, Locale.ENGLISH, "Title is required", "title");
        assertExpectedViolation(underTest, context, Locale.FRENCH, "Le titre est requis", "title");

    }

    /**
     * Given : a valid classified valued with an invalid description<br/>
     * When : one persists the above classified<br/>
     * Then : system should throw a {@link javax.validation.ConstraintViolationException}<br/>
     */
    private void shouldValidateTitleSizeConstraint(final ValidationContext context) {
        // Given
        underTest = TestFixtures.validClassified();
        final String wrongData = RandomStringUtils.random(Classified.CONSTRAINT_TITLE_MAX_SIZE + 1);
        underTest.setTitle(wrongData);

        assertExpectedViolation(underTest, context, Locale.ENGLISH, "Title max length is "
                + Classified.CONSTRAINT_DESCRIPTION_MAX_SIZE, "title");
        assertExpectedViolation(underTest, context, Locale.FRENCH, "Longueur max pour le titre: "
                + Classified.CONSTRAINT_DESCRIPTION_MAX_SIZE, "title");

    }

    /**
     * Given : a valid classified valued with an invalid main offer<br/>
     * When : one persists the above classified<br/>
     * Then : system should throw a {@link javax.validation.ConstraintViolationException}<br/>
     */
    private void shouldValidateDescriptionRequiredConstraint(final ValidationContext context) {
        // Given
        String wrongData;
        underTest = TestFixtures.validClassified();

        wrongData = null;
        underTest.setDescription(wrongData);

        assertExpectedViolation(underTest, context, Locale.ENGLISH, "Description is required", "description");
        assertExpectedViolation(underTest, context, Locale.FRENCH, "La description est requise", "description");

        wrongData = "";
        underTest.setDescription(wrongData);

        assertExpectedViolation(underTest, context, Locale.ENGLISH, "Description is required", "description");
        assertExpectedViolation(underTest, context, Locale.FRENCH, "La description est requise", "description");

    }

    /**
     * Given : a valid classified valued with an invalid description<br/>
     * When : one persists the above classified<br/>
     * Then : system should throw a {@link javax.validation.ConstraintViolationException}<br/>
     */
    private void shouldValidateDescriptionSizeConstraint(final ValidationContext context) {
        // Given
        underTest = TestFixtures.validClassified();
        final String wrongData = RandomStringUtils.random(Classified.CONSTRAINT_DESCRIPTION_MAX_SIZE + 1);
        underTest.setDescription(wrongData);

        assertExpectedViolation(underTest, context, Locale.ENGLISH, "Description max length is "
                + Classified.CONSTRAINT_DESCRIPTION_MAX_SIZE, "description");
        assertExpectedViolation(underTest, context, Locale.FRENCH, "Longueur max pour la description : "
                + Classified.CONSTRAINT_DESCRIPTION_MAX_SIZE, "description");

    }

    /**
     * Validate "create classified use case"
     */
    @Test
    public void shouldValidateClassifiedForCreateContext() {
        shouldValidateTitleRequiredConstraint(ValidationContext.CREATE);
        shouldValidateDescriptionSizeConstraint(ValidationContext.CREATE);
    }

    /**
     * Validate "update classified use case"
     */
    @Test
    public void shouldValidateClassifiedForUpdateContext() {
        shouldValidateTitleRequiredConstraint(ValidationContext.UPDATE);
        shouldValidateDescriptionSizeConstraint(ValidationContext.UPDATE);
    }

}
