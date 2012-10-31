/*
 *
 */
package org.diveintojee.poc.domain.business;

import org.diveintojee.poc.domain.Account;
import org.diveintojee.poc.domain.Restaurant;

import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
public interface Facade {

    String BEAN_ID = "facade";

    /**
     * @param account
     * @return
     */
    Long createAccount(Account account);

    /**
     * @param accountId
     * @param restaurant
     * @return
     */
    Long createRestaurant(final Long accountId, final Restaurant restaurant);

    /**
     * @param accountId
     */
    void deleteAccount(Long accountId);

    /**
     * @param foodSpecialtyId
     * @throws javax.validation.ConstraintViolationException
     */
    void deleteFoodSpecialty(Long foodSpecialtyId);

    /**
     * @param accountId
     * @param restaurantId
     * @throws javax.validation.ConstraintViolationException
     */
    void deleteRestaurant(Long accountId, Long restaurantId);

    /**
     * @param criteria
     * @return
     */
    List<Restaurant> findRestaurantsByCriteria(Restaurant criteria);

    /**
     * @param foodSpecialtyId
     */
    void inactivateFoodSpecialty(Long foodSpecialtyId);

    /**
     * @param id
     * @return Account readAccount(Long id);
     */

    /**
     * @param accountId
     * @param initializeCollections
     * @return
     */
    Account readAccount(Long accountId, boolean initializeCollections);

    /**
     * @param accountId
     * @param restaurantId
     * @param initializeCollections
     * @return
     */
    Restaurant readRestaurant(Long accountId, Long restaurantId, boolean initializeCollections);

    /**
     * @param account
     */
    void updateAccount(Long accountId, Account account);

    /**
     * @param restaurantId
     * @param accountId
     * @param restaurant
     * @throws javax.validation.ConstraintViolationException
     */
    void updateRestaurant(Long accountId, Long restaurantId, Restaurant restaurant);

    /**
     * @param authorityId
     */
    void inactivateAuthority(Long authorityId);

    /**
     * @param accountId
     */
    void lockAccount(Long accountId);

}
