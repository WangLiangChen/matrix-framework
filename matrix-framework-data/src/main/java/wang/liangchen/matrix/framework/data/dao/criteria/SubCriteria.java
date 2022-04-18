package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class SubCriteria<T extends RootEntity> extends AbstractCriteria<T> {
    private SubCriteria(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T extends RootEntity> SubCriteria<T> of(Class<T> entityClass) {
        return new SubCriteria<T>(entityClass) {
        };
    }

    @Override
    public SubCriteria<T> equals(EntityGetter<T> column, SqlValue sqlValue) {
        return (SubCriteria<T>) super.equals(column, sqlValue);
    }

    @Override
    public SubCriteria<T> OR(SubCriteria<T> subCriteria) {
        return (SubCriteria<T>) super.OR(subCriteria);
    }

    @Override
    public SubCriteria<T> AND(SubCriteria<T> subCriteria) {
        return (SubCriteria<T>) super.AND(subCriteria);
    }
}
