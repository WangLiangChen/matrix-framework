package wang.liangchen.matrix.framework.data.pagination;

import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiangChen.Wang
 */
public class PaginationParameter extends EnhancedObject {
    /**
     * 分页页号
     */
    private transient Integer page;
    /**
     * 分页记录偏移(MySql)
     */
    private transient Integer offset;
    /**
     * 行数
     */
    private transient Integer rows;
    /**
     * 排序
     */
    private transient List<OrderBy> orderBy;

    public void initPagination() {
        this.page = null == this.page ? 1 : this.page;
        this.rows = null == this.rows ? 10 : this.rows;
    }

    public List<OrderBy> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<OrderBy> orderBy) {
        Assert.INSTANCE.notEmpty(orderBy, "参数orderBy不能为空");
        this.orderBy = orderBy;
    }

    public void addOrderBy(String orderby, OrderByDirection direction) {
        addOrderBy(orderby, direction, 0);
    }

    public void addOrderBy(String orderby, OrderByDirection direction, Integer index) {
        Assert.INSTANCE.notBlank(orderby, "参数orderby不能为空");
        Assert.INSTANCE.notNull(direction, "参数direction不能为空");
        orderBy = orderBy == null ? new ArrayList<>(1) : orderBy;
        if (null == index) {
            orderBy.add(new OrderBy(orderby, direction));
            return;
        }
        orderBy.add(index, new OrderBy(orderby, direction));
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
}
