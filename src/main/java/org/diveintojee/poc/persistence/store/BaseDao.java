/**
 *
 */
package org.diveintojee.poc.persistence.store;

import java.util.List;
import java.util.Map;

/**
 * @author louis.gueye@gmail.com
 */
public interface BaseDao {

    String BEAN_ID = "baseDao";

    /**
     * @param <T>
     * @param entityClass
     * @return
     */
    <T> int countAll(Class<T> entityClass);

    /**
     * @param <T>
     * @param exampleInstance
     * @return
     */
    <T> int countByExample(final T exampleInstance);

    /**
     *
     * @param entityClass
     * @param id
     * @param <T>
     */
    <T> void delete(Class<T> entityClass, Object id);

    /**
     *
     * @param attachedInstance
     */
    void evict(Object attachedInstance);

    /**
     * @param <T>
     * @param entityClass
     * @return
     */
    <T> List<T> findAll(Class<T> entityClass);

    /**
     * @param <T>
     * @param exampleInstance
     * @return
     */
    <T> List<T> findByExample(final T exampleInstance);

    /**
     * @param <T>
     * @param name
     * @param params
     * @return
     */
    <T> List<T> findByNamedQuery(final String name, Object... params);

    /**
     * @param <T>
     * @param name
     * @param params
     * @return
     */
    <T> List<T> findByNamedQueryAndNamedParams(final String name,
                                               final Map<String, ? extends Object> params);

    void flush();

    /**
     * @param <T>
     * @param entityClass
     * @param id
     * @return
     */
    <T> T get(Class<T> entityClass, Object id);

    /**
     * @param entity
     */
    void merge(Object entity);

    /**
     * @param entity
     */
    void persist(Object entity);

    /**
     * @param entity
     */
    void refresh(Object entity);

}
