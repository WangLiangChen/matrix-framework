package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 * _equals         _notEquals
 * _in             _notIn
 * _greaterThan    _greaterThanOrEquals
 * _lessThan       _lessThanOrEquals
 * _isNull         _isNotNull
 * _between        _notBetween
 * _startWith      _notStartWith
 * _endWith        _notEndWith
 * _contains       _notContains
 */
abstract class AbstractCriteria<E extends RootEntity> extends AbstractObjectCriteria<E> {


    protected AbstractCriteria(E entity) {
        super(entity);
    }

    public AbstractCriteria(Class<E> entityClass) {
        super(entityClass);
    }
}
