package wang.liangchen.matrix.framework.data.criteria;


import wang.liangchen.matrix.framework.data.dao.criteria.EntityGetter;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class UpdateCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private Map<EntityGetter<E>, Object> forceUpdateFields;

    @SuppressWarnings("unchecked")
    private UpdateCriteria(E entity) {
        super(entity);
    }
    private UpdateCriteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <E extends RootEntity> UpdateCriteria<E> of(E entity) {
        return new UpdateCriteria<E>(entity) {
        };
    }
    public static <E extends RootEntity> UpdateCriteria<E> of(Class<E> entityClass) {
        return new UpdateCriteria<E>(entityClass) {
        };
    }

    public UpdateCriteria<E> forceUpdate(EntityGetter<E> column, Object value) {
        if (null == forceUpdateFields) {
            forceUpdateFields = new HashMap<>();
        }
        forceUpdateFields.put(column, value);
        return this;
    }

    protected Map<EntityGetter<E>, Object> getForceUpdateFields() {
        return forceUpdateFields;
    }
}
