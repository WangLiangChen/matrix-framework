package wang.liangchen.matrix.framework.data.criteria;


import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class SubCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private SubCriteria(E entity) {
        super(entity);
    }

    private SubCriteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <E extends RootEntity> SubCriteria<E> of(E entity) {
        return new SubCriteria<E>(entity) {
        };
    }

    public static <E extends RootEntity> SubCriteria<E> of(Class<E> entityClass) {
        return new SubCriteria<E>(entityClass) {
        };
    }
}
