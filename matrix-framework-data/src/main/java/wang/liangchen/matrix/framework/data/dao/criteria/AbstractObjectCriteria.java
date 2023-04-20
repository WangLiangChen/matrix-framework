package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.function.Consumer;

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
abstract class AbstractObjectCriteria<E extends RootEntity> extends AbstractClassCriteria<E> {

    private final E entity;

    @SuppressWarnings("unchecked")
    protected AbstractObjectCriteria(E entity) {
        super((Class<E>) ValidationUtil.INSTANCE.notNull(entity).getClass());
        this.entity = entity;
    }

    @SuppressWarnings("unchecked")
    protected AbstractObjectCriteria(E entity, AndOr andOr) {
        super((Class<E>) ValidationUtil.INSTANCE.notNull(entity).getClass(), andOr);
        this.entity = entity;
    }

    protected AbstractObjectCriteria(Class<E> entityClass) {
        super(entityClass);
        this.entity = null;
    }

    protected AbstractObjectCriteria(Class<E> entityClass, AndOr andOr) {
        super(entityClass, andOr);
        this.entity = null;
    }

    protected AbstractObjectCriteria<E> _equals(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.EQUALS, fieldGetter);
        return this;
    }

    protected AbstractObjectCriteria<E> _notEquals(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.NOTEQUALS, fieldGetter);
        return this;
    }

    protected AbstractObjectCriteria<E> _greaterThan(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.GREATERTHAN, fieldGetter);
        return this;
    }

    protected AbstractObjectCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.GREATERTHAN_OR_EQUALS, fieldGetter);
        return this;
    }

    protected AbstractObjectCriteria<E> _lessThan(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.LESSTHAN, fieldGetter);
        return this;
    }

    protected AbstractObjectCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.LESSTHAN_OR_EQUALS, fieldGetter);
        return this;
    }

    protected AbstractObjectCriteria<E> _or(Consumer<SubCriteria<E>> consumer) {
        SubCriteria<E> subCriteria = subCriteria(AndOr.or);
        consumer.accept(subCriteria);
        this.getComposedCriteriaResolver().add(subCriteria.getComposedCriteriaResolver());
        return this;
    }
    protected AbstractObjectCriteria<E> _or() {
        this.getComposedCriteriaResolver().add(OrCriteriaResolver.newInstance());
        return this;
    }

    protected AbstractObjectCriteria<E> _and(Consumer<SubCriteria<E>> consumer) {
        SubCriteria<E> subCriteria = subCriteria(AndOr.and);
        consumer.accept(subCriteria);
        this.getComposedCriteriaResolver().add(subCriteria.getComposedCriteriaResolver());
        return this;
    }

    private SubCriteria<E> subCriteria(AndOr andOr) {
        if (null == this.getEntity()) {
            return SubCriteria.newInstance(this.getEntityClass(), andOr);
        }
        return SubCriteria.newInstance(this.getEntity(), andOr);
    }

    private void addCriteriaMeta(Operator operator, EntityGetter<E> fieldGetter) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, entity, "Unsupported call,entity must not be null");
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(fieldGetter);
        String getterName = StringUtil.INSTANCE.getGetter(fieldName);
        Object fieldValue = ClassUtil.INSTANCE.invokeGetter(entity, getterName);
        ColumnMeta columnMeta = getColumnMetas().get(fieldName);
        getComposedCriteriaResolver().add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), operator, fieldValue));
    }

    protected E getEntity() {
        return entity;
    }
}
