/*
 *
 */
package org.diveintojee.poc.domain.business;


import org.diveintojee.poc.domain.Classified;

import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
public interface Facade {

    String BEAN_ID = "facade";

    Long createClassified(Classified classified);

    Classified readClassified(Long id);

    void updateClassified(Long id, Classified classified);

    void deleteClassified(Long id);

    List<Classified> findClassifiedsByCriteria(Classified criteria);

}
