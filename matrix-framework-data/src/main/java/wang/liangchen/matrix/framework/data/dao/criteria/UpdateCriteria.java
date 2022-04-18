package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.OrderByDirection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class UpdateCriteria<T extends RootEntity> extends AbstractCriteria<T> {
    @SuppressWarnings("unchecked")
    private UpdateCriteria(T entity) {
        super((Class<T>) entity.getClass());
    }

    public static <T extends RootEntity> UpdateCriteria<T> of(T entity) {
        return new UpdateCriteria<T>(entity) {
        };
    }

    @Override
    public UpdateCriteria<T> equals(EntityGetter<T> column, SqlValue sqlValue) {
        return (UpdateCriteria<T>) super.equals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<T> OR(SubCriteria<T> subCriteria) {
        return (UpdateCriteria<T>) super.OR(subCriteria);
    }

    @Override
    public UpdateCriteria<T> AND(SubCriteria<T> subCriteria) {
        return (UpdateCriteria<T>) super.AND(subCriteria);
    }
}
