package liangchen.wang.matrix.framework.data.query;


import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import liangchen.wang.matrix.framework.data.query.Operator;
import liangchen.wang.matrix.framework.data.pagination.PaginationParameter;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author LiangChen.Wang
 */
public abstract class RootQuery extends PaginationParameter {

    /**
     * 是否拼接distinct
     */
    @Transient
    private transient Boolean distinct;
    /**
     * 是否拼接for update
     */
    @Transient
    private transient Boolean forUpdate;
    /**
     * 是否使用分页拦截器
     */
    @Transient
    private transient Boolean autoPagination;
    /**
     * 使用拦截器分页时返回总记录数
     */
    @Transient
    private transient Integer totalRecords;
    /**
     * 动态查询条件
     */
    @Transient
    private transient List<Criteria> criterias;

    /**
     * 全局搜索关键字，如何使用由后端确定
     */
    @Transient
    private String keyword;

    /**
     * 返回的字段
     */
    @Transient
    private transient String[] returnFields;

    /**
     * 实体对象，主要用于update操作
     */
    @Transient
    private RootEntity entity;

    public Boolean getAutoPagination() {
        return autoPagination;
    }

    public void setAutoPagination(Boolean autoPagination) {
        this.autoPagination = autoPagination;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Boolean getForUpdate() {
        return forUpdate;
    }

    public void setForUpdate(Boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    public String[] getReturnFields() {
        return returnFields;
    }

    public void setReturnFields(String[] returnFields) {
        this.returnFields = returnFields;
    }

    public RootEntity getEntity() {
        return entity;
    }

    public void setEntity(RootEntity entity) {
        this.entity = entity;
    }

    public List<Criteria> getCriterias() {
        return criterias;
    }

    public static class Criterion {
        private String field;
        private String condition;
        private Object value;
        private Object secondValue;
        private boolean noValue;
        private boolean singleValue;
        private boolean betweenValue;
        private boolean listValue;
        private String typeHandler;

        public String getField() {
            return field;
        }

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        /**
         * 无值，如is null；not null；
         *
         */
        protected Criterion(String field, String condition) {
            super();
            this.field = field;
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        /**
         * 单值，如=，！=
         *
         */
        protected Criterion(String field, String condition, Object value, String typeHandler) {
            super();
            this.field = field;
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof Collection) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String field, String condition, Object value) {
            this(field, condition, value, null);
        }

        /**
         * 两值，如between
         *
         */
        protected Criterion(String field, String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.field = field;
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String field, String condition, Object value, Object secondValue) {
            this(field, condition, value, secondValue, null);
        }
    }

    public static class Criteria {
        protected List<Criterion> criterions;

        protected Criteria() {
            criterions = new ArrayList<>();
        }

        public boolean isValid() {
            return criterions.size() > 0;
        }

        public List<Criterion> getCriterions() {
            return criterions;
        }

        public Criteria add(String field, Operator operator, Object value) {
            criterions.add(new Criterion(field, operator.getOperator(), value));
            return this;
        }

        public Criteria add(String field, Operator operator) {
            criterions.add(new Criterion(field, operator.getOperator()));
            return this;
        }

        public Criteria add(String field, Operator operator, Object value, Object secondValue) {
            criterions.add(new Criterion(field, operator.getOperator(), value, secondValue));
            return this;
        }

        public Criteria isNull(String field) {
            criterions.add(new Criterion(field, Operator.ISNULL.getOperator()));
            return this;
        }

        public Criteria notNull(String field) {
            criterions.add(new Criterion(field, Operator.ISNOTNULL.getOperator()));
            return this;
        }

        public Criteria equals(String field, Object value) {
            criterions.add(new Criterion(field, Operator.EQUALS.getOperator(), value));
            return this;
        }

        public Criteria notEquals(String field, Object value) {
            criterions.add(new Criterion(field, Operator.NOTEQUALS.getOperator(), value));
            return this;
        }

        public Criteria in(String field, Object value) {
            criterions.add(new Criterion(field, Operator.IN.getOperator(), value));
            return this;
        }

        public Criteria notIn(String field, Object value) {
            criterions.add(new Criterion(field, Operator.NOTIN.getOperator(), value));
            return this;
        }
    }

    public Criteria or() {
        Criteria criteria = new Criteria();
        if (null == criterias) {
            criterias = new ArrayList<>();
        }
        criterias.add(criteria);
        return criteria;
    }

}
