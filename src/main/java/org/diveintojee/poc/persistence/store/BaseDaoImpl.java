/**
 *
 */
package org.diveintojee.poc.persistence.store;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

/**
 * @author louis.gueye@gmail.com
 */
@Repository(BaseDao.BEAN_ID)
public class BaseDaoImpl implements BaseDao {

    @PersistenceContext(unitName = JpaConstants.PERSISTANCE_UNIT_NAME)
    private EntityManager entityManager;

    /**
     * @see BaseDao#delete(Class, Object)
     */
    @Override
    public <T> void delete(final Class<T> entityClass, final Object id) {
        final Object entity = get(entityClass, id);
        this.entityManager.remove(entity);
    }

    /**
     * @see BaseDao#evict(Object)
     */
    @Override
    public void evict(final Object attachedInstance) {
        ((Session) this.entityManager.getDelegate()).evict(attachedInstance);
    }

    /**
     * @see BaseDao#findAll(Class)
     */
    @Override
    public <T> List<T> findAll(final Class<T> entityClass) {
        return findByCriteria(entityClass);
    }

    /**
     * Use this inside subclasses as a convenience method.
     *
     * @param <T>
     * @param entityClass
     * @param criterion
     * @return
     */
    <T> List<T> findByCriteria(final Class<T> entityClass, final Criterion... criterion) {
        return findByCriteria(entityClass, -1, -1, criterion);
    }

    /**
     * Use this inside subclasses as a convenience method.
     *
     * @param <T>
     * @param entityClass
     * @param firstResult
     * @param maxResults
     * @param criterion
     * @return
     */
    @SuppressWarnings("unchecked")
    <T> List<T> findByCriteria(final Class<T> entityClass, final int firstResult, final int maxResults,
                                         final Criterion... criterion) {
        final Session session = (Session) this.entityManager.getDelegate();
        final Criteria crit = session.createCriteria(entityClass);

        for (final Criterion c : criterion)
            crit.add(c);

        if (firstResult > 0)
            crit.setFirstResult(firstResult);

        if (maxResults > 0)
            crit.setMaxResults(maxResults);

        final List<T> result = crit.list();
        return result;
    }

    /**
     * @see BaseDao#flush()
     */
    @Override
    public void flush() {
        this.entityManager.flush();
    }

    /**
     * @see BaseDao#get(Class, Object)
     */
    @Override
    public <T> T get(final Class<T> entityClass, final Object id) {
        return this.entityManager.find(entityClass, id);
    }

    /**
     * @see BaseDao#merge(Object)
     */
    @Override
    public void merge(final Object entity) {
        this.entityManager.merge(entity);
    }

    /**
     * @see BaseDao#persist(Object)
     */
    @Override
    public void persist(final Object entity) {
        this.entityManager.persist(entity);
    }

}
