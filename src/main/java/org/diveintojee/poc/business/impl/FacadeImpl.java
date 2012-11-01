/*
 *
 */
package org.diveintojee.poc.business.impl;

import org.diveintojee.poc.domain.Account;
import org.diveintojee.poc.domain.business.Facade;
import org.diveintojee.poc.domain.business.Validator;
import org.diveintojee.poc.persistence.search.SearchEngine;
import org.diveintojee.poc.persistence.store.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * @author louis.gueye@gmail.com
 */
@Service(Facade.BEAN_ID)
public class FacadeImpl implements Facade {

    @Autowired
    private Validator validator;

    @Autowired
    private BaseDao baseDao;

    @Autowired
    @Qualifier("messageSources")
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(FacadeImpl.class);

    @Autowired
    private SearchEngine searchEngine;


    @Override
    public Long createAccount(Account account) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
