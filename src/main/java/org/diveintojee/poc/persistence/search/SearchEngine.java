package org.diveintojee.poc.persistence.search;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.Classified;

import java.io.IOException;
import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
public interface SearchEngine {

    String CLASSIFIEDS_ALIAS = SearchIndices.classifieds.toString();

    String CLASSIFIED_TYPE = SearchTypes.classified.toString();

    List<Classified> findClassifiedsByCriteria(Classified criteria);

    void index(AbstractEntity entity);

    void removeFromIndex(AbstractEntity entity);

    void reIndexClassifieds() throws IOException;
}
