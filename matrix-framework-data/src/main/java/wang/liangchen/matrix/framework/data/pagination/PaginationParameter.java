package wang.liangchen.matrix.framework.data.pagination;

import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.object.EnhancedMap;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiangChen.Wang
 */
public class PaginationParameter extends EnhancedMap {
    /**
     * 分页页号
     */
    @Transient
    private transient Integer pageNumber;
    /**
     * 分页记录偏移(MySql)
     */
    @Transient
    private transient Integer offset;
    /**
     * 行数
     */
    @Transient
    private transient Integer pageSize;
    @Transient
    private transient Integer rows;
    /**
     * 排序
     */
    @Transient
    private transient List<OrderBy> orderBys;
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

    public void initPagination() {
        this.pageNumber = null == this.pageNumber ? 1 : this.pageNumber;
        this.pageSize = null == this.pageSize ? 10 : this.pageSize;
    }

    public void addOrderBy(String orderby, OrderByDirection direction, Integer index) {
        Assert.INSTANCE.notBlank(orderby, "orderby can not be blank");
        Assert.INSTANCE.notNull(direction, "direction can not be null");
        if (null == orderBys) {
            orderBys = new ArrayList<>();
        }
        if (null == index) {
            orderBys.add(new OrderBy(orderby, direction));
            return;
        }
        orderBys.add(index, new OrderBy(orderby, direction));
    }

    public void addOrderBy(String orderby, OrderByDirection direction) {
        addOrderBy(orderby, direction, null);
    }

    public void addOrderBys(List<OrderBy> orderBys) {
        Assert.INSTANCE.notEmpty(orderBys, "orderBys can not be empty");
        if (null == this.orderBys) {
            this.orderBys = new ArrayList<>();
        }
        this.orderBys.addAll(orderBys);
    }

    public void addResultColumn(String resultColumn) {
        Assert.INSTANCE.notBlank(resultColumn, "resultColumn can not be blank");
        if (null == resultColumns) {
            resultColumns = new ArrayList<>();
        }
        resultColumns.add(resultColumn);
    }

    public void addResultColumns(List<String> resultColumns) {
        Assert.INSTANCE.notEmpty(resultColumns, "resultColumns can not be empty");
        if (null == this.resultColumns) {
            this.resultColumns = new ArrayList<>();
        }
        this.resultColumns.addAll(resultColumns);
    }

    public Integer getPageNumber() {
        if (null != pageNumber) {
            pageNumber = pageNumber < 1 ? 1 : pageNumber;
            return pageNumber;
        }
        if (null == offset || null == pageSize) {
            return 1;
        }
        pageNumber = offset / pageSize + 1;
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getOffset() {
        if (null != offset) {
            offset = offset < 0 ? 0 : offset;
            return offset;
        }
        if (null != pageNumber && null != pageSize) {
            offset = (pageNumber - 1) * pageSize;
        }
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getPageSize() {
        if (null == pageSize) {
            return 10;
        }
        if (pageSize < 1) {
            return 1;
        }
        return pageSize;
    }

    public Integer getRows() {
        return getPageSize();
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
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

    public List<OrderBy> getOrderBys() {
        return orderBys;
    }

}
