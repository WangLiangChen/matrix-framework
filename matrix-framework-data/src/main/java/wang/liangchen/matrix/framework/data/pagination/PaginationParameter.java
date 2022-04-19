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
    private transient Integer page;
    /**
     * 分页记录偏移(MySql)
     */
    @Transient
    private transient Integer offset;
    /**
     * 行数
     */
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
        this.page = null == this.page ? 1 : this.page;
        this.rows = null == this.rows ? 10 : this.rows;
    }

    public void addOrderBy(String orderby, OrderByDirection direction, Integer index) {
        Assert.INSTANCE.notBlank(orderby, "参数orderby不能为空");
        Assert.INSTANCE.notNull(direction, "参数direction不能为空");
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
        Assert.INSTANCE.notBlank(resultColumn, "参数resultColumn不能为空");
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

    public Integer getPage() {
        if (null != page) {
            page = page < 1 ? 1 : page;
            return page;
        }
        if (null == offset || null == rows) {
            return 1;
        }
        page = offset / rows + 1;
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getOffset() {
        if (null != offset) {
            offset = offset < 0 ? 0 : offset;
            return offset;
        }
        if (null != page && null != rows) {
            offset = (page - 1) * rows;
        }
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getRows() {
        if (null == rows) {
            return 10;
        }
        if (rows < 1) {
            return 1;
        }
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
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
