/*
 *
 */
package org.diveintojee.poc.domain.business;

import org.diveintojee.poc.domain.Account;

/**
 * @author louis.gueye@gmail.com
 */
public interface Facade {

    String BEAN_ID = "facade";

    Long createAccount(Account account);
}
