package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class SubCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private SubCriteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <E extends RootEntity> SubCriteria<E> of(Class<E> entityClass) {
        return new SubCriteria<E>(entityClass) {
        };
    }

    @Override
    public SubCriteria<E> equals(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super.equals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> OR(SubCriteria<E> subCriteria) {
        return (SubCriteria<E>) super.OR(subCriteria);
    }

    @Override
    public SubCriteria<E> AND(SubCriteria<E> subCriteria) {
        return (SubCriteria<E>) super.AND(subCriteria);
    }
}
