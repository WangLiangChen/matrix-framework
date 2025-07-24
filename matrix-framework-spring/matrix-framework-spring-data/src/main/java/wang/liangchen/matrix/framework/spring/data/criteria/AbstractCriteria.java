package wang.liangchen.matrix.framework.spring.data.criteria;


import wang.liangchen.matrix.framework.spring.data.entity.RootEntity;

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

    private final CriteriaParameter<E> criteriaParameter;

    protected AbstractCriteria(E entity) {
        super(entity);
        criteriaParameter = new CriteriaParameter<>(entity);
    }

    public AbstractCriteria(Class<E> entityClass) {
        super(entityClass);
        criteriaParameter = new CriteriaParameter<>(entityClass);
    }

    protected CriteriaParameter<E> getCriteriaParameter() {
        return criteriaParameter;
    }
}
