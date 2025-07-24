package wang.liangchen.matrix.framework.data.pagination;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

;

/**
 * @author LiangChen.Wang
 */
public class Pagination implements Serializable {
    /**
     * 分页页号
     */
    private Integer pageNumber;
    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 排序
     */
    private List<OrderBy> orderBys;

    /**
     * 分页记录偏移
     */
    private Integer offset;
    /**
     * 行数
     */
    private Integer rows;

    public Pagination() {
    }

    public Pagination(Integer pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }


    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageNumber() {
        if (null == pageNumber) {
            return null;
        }
        pageNumber = pageNumber < 1 ? 1 : pageNumber;
        return pageNumber;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageSize() {
        if (null == pageSize) {
            return null;
        }
        pageSize = pageSize < 1 ? 10 : pageSize;
        return pageSize;
    }


    public Integer getOffset() {
        if (null == pageNumber || null == pageSize) {
            return null;
        }
        this.offset = (getPageNumber() - 1) * getPageSize();
        return offset;
    }

    public Integer getRows() {
        this.rows = getPageSize();
        return rows;
    }

    public List<OrderBy> getOrderBys() {
        return orderBys;
    }

    public Pagination setOrderBys(List<OrderBy> orderBys) {
        this.orderBys = orderBys;
        return this;
    }

    public void addOrderBy(String orderBy, OrderByDirection orderByDirection, Integer index) {
        ValidationUtil.INSTANCE.notNullAndBlank(orderBy, "The parameter 'orderBy' must not be blank");
        if (null == this.orderBys) {
            this.orderBys = new ArrayList<>();
        }
        if (null == index) {
            this.orderBys.add(new OrderBy(orderBy, orderByDirection));
            return;
        }
        this.orderBys.add(index, new OrderBy(orderBy, orderByDirection));
    }

    public Pagination addOrderBy(String orderby, OrderByDirection direction) {
        addOrderBy(orderby, direction, null);
        return this;
    }

    public void addOrderBys(List<OrderBy> orderBys) {
        ValidationUtil.INSTANCE.notNullAndEmpty(orderBys, "The parameter 'orderBys' must not be empty");
        if (null == this.orderBys) {
            this.orderBys = new ArrayList<>();
        }
        this.orderBys.addAll(orderBys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize, orderBys);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Pagination that = (Pagination) object;
        return Objects.equals(pageNumber, that.pageNumber) && Objects.equals(pageSize, that.pageSize) && Objects.equals(orderBys, that.orderBys);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Pagination[", "]")
                .add("pageNumber=" + pageNumber)
                .add("pageSize=" + pageSize)
                .add("orderBys=" + orderBys)
                .toString();
    }
}
