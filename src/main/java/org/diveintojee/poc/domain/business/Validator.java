/*
 *
 */
package org.diveintojee.poc.domain.business;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.validation.ValidationContext;

import fr.midipascher.domain.AbstractEntity;
import fr.midipascher.domain.validation.ValidationContext;

/**
 * @author louis.gueye@gmail.com
 */
public interface Validator {

    /**
     * @param type
     * @param context
     */
    <T extends AbstractEntity> void validate(T type, ValidationContext context);

}
