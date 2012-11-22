/*
 *
 */
package org.diveintojee.poc.business.impl;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Facade;
import org.diveintojee.poc.domain.business.Validator;
import org.diveintojee.poc.domain.exceptions.BusinessException;
import org.diveintojee.poc.domain.validation.ValidationContext;
import org.diveintojee.poc.integration.ClassifiedsConsumer;
import org.diveintojee.poc.persistence.search.SearchEngine;
import org.diveintojee.poc.persistence.store.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
@Service(Facade.BEAN_ID)
public class FacadeImpl implements Facade {

    @Autowired
    private Validator validator;

    @Autowired
    private BaseDao baseDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(FacadeImpl.class);

    @Autowired
    private SearchEngine searchEngine;

    @Autowired
    private ClassifiedsConsumer classifiedsConsumer;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long createClassified(Classified classified) {
        if (classified == null) {
          String message = "create classified - classified is required";
          LOGGER.error(message);
          throw new IllegalArgumentException(message);
        }
        this.validator.validate(classified, ValidationContext.CREATE);
        this.baseDao.persist(classified);
        return classified.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateClassified(Long id, Classified classified) {
        Classified persisted = readClassified(id);
        persisted.setTitle(classified.getTitle());
        persisted.setDescription(classified.getDescription());
        this.validator.validate(persisted, ValidationContext.UPDATE);
    }

    @Override
    @Transactional(readOnly = true)
    public Classified readClassified(Long id) {
        if (id == null) {
          String message = "read classified - classified id is required";
          LOGGER.error(message);
          throw new IllegalArgumentException(message);
        }
        Classified classified = baseDao.get(Classified.class, id);
        if (classified == null) {
          String message = "read classified - Classified [id = {0}] was not found";
          LOGGER.error(message);
          throw new BusinessException("classified.not.found", null, message);
        }
        return classified;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteClassified(Long id) {
        if (id == null) {
          String message = "read classified - classified id is required";
          LOGGER.error(message);
          throw new IllegalArgumentException(message);
        }
        Classified classified = baseDao.get(Classified.class, id);
        if (classified != null) {
          baseDao.delete(Classified.class, id);
        }
    }

    @Override
    public List<Classified> findClassifiedsByCriteria(Classified criteria) {
        if (criteria == null) {
          String message = "search classified - classified is required";
          LOGGER.error(message);
          throw new IllegalArgumentException(message);
        }
        return searchEngine.findClassifiedsByCriteria(criteria);
    }

    @Override
    public void fullReIndexClassifieds() {
        searchEngine.reIndexClassifieds();
    }

    @Override
    public void startConsumingClassifieds() {
        classifiedsConsumer.startConsumingClassifieds();
    }

    @Override
    public void stopConsumingClassifiedsWriteCommands() {
        classifiedsConsumer.stopConsumingWriteCommands();
    }

}
