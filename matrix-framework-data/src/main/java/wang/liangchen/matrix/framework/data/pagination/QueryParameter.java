package wang.liangchen.matrix.framework.data.pagination;

import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.object.EnhancedMap;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private transient List<String> resultColumns;

    public void addResultColumn(String resultColumn) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN, resultColumn, "resultColumn must not be blank");
        if (null == resultColumns) {
            resultColumns = new ArrayList<>();
        }
        resultColumns.add(resultColumn);
    }

    public void addResultColumns(List<String> resultColumns) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, resultColumns, "resultColumns must not be empty");
        if (null == resultColumns) {
            resultColumns = new ArrayList<>();
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

    public List<String> getResultColumns() {
        return resultColumns;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
