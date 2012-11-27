/**
 *
 */
package org.diveintojee.poc.persistence.store;

import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
public interface BaseDao {

    String BEAN_ID = "baseDao";

    /**
     * @param entityClass
     * @param id
     * @param <T>
     */
    <T> void delete(Class<T> entityClass, Object id);

    /**
     * @param attachedInstance
     */
    void evict(Object attachedInstance);

    /**
     * @param <T>
     * @param entityClass
     * @return
     */
    <T> List<T> findAll(Class<T> entityClass);

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

}
