package org.diveintojee.poc.persistence.search;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.Account;

import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
public interface SearchEngine {

    String INDEX_NAME = SearchIndices.midipascher.toString();

    String RESTAURANT_TYPE_NAME = SearchTypes.restaurant.toString();

    List<Account> findAccountsByCriteria(Account criteria);

    void index(AbstractEntity entity);

    void removeFromIndex(AbstractEntity entity);
}
