package wang.liangchen.matrix.framework.data.criteria;

import jakarta.persistence.Transient;
import wang.liangchen.matrix.framework.commons.object.EnhancedList;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.data.pagination.Pagination;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author LiangChen.Wang
 */
class QueryParameter extends EnhancedObject {
    @Transient
    private final transient List<String> selectColumns = new EnhancedList<>();
    @Transient
    private final transient Pagination pagination = new Pagination();
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


    public void addSelectColumn(String selectColumn) {
        selectColumns.add(selectColumn);
    }

    public void addSelectColumns(Collection<String> selectColumns) {
        this.selectColumns.addAll(selectColumns);
    }

    public List<String> getSelectColumns() {
        return selectColumns;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    public Boolean getForUpdate() {
        return forUpdate;
    }

    public void setForUpdate(Boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "QueryParameter[", "]")
                .add("pagination=" + pagination)
                .add("distinct=" + distinct)
                .add("forUpdate=" + forUpdate)
                .add("selectColumns=" + selectColumns)
                .add(super.toString())
                .toString();
    }
}
