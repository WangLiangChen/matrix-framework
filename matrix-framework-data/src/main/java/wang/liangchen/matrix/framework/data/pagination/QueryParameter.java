package wang.liangchen.matrix.framework.data.pagination;

import jakarta.persistence.Transient;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author LiangChen.Wang
 */
public class QueryParameter extends EnhancedObject {

    @Transient
    private transient Pagination pagination = new Pagination();
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
    @Transient
    private transient Set<String> resultColumns;

    public void addResultColumn(String resultColumn) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN, resultColumn, "resultColumn must not be blank");
        if (null == resultColumns) {
            resultColumns = new HashSet<>();
        }
        resultColumns.add(resultColumn);
    }

    public void addResultColumns(Set<String> resultColumns) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, resultColumns, "resultColumns must not be empty");
        if (null == this.resultColumns) {
            this.resultColumns = new HashSet<>();
        }
        this.resultColumns.addAll(resultColumns);
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Boolean getForUpdate() {
        return forUpdate;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    public void setForUpdate(Boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    public Set<String> getResultColumns() {
        return resultColumns;
    }

    public Pagination getPagination() {
        return pagination;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QueryParameter.class.getSimpleName() + "[", "]")
                .add("pagination=" + pagination)
                .add("distinct=" + distinct)
                .add("forUpdate=" + forUpdate)
                .add("resultColumns=" + resultColumns)
                .toString();
    }
}
