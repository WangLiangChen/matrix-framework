package wang.liangchen.matrix.framework.data.pagination;

import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
     * 分页记录偏移(MySql)
     */
    private Integer offset;
    /**
     * 行数
     */
    private Integer pageSize;

    private Integer rows;
    /**
     * 排序
     */

    private List<OrderBy> orderBys;

    public static Pagination newInstance() {
        return ClassUtil.INSTANCE.instantiate(Pagination.class);
    }

    public Integer getPageNumber() {
        if (null == pageNumber || null == pageSize) {
            return null;
        }
        pageNumber = pageNumber < 1 ? 1 : pageNumber;
        return pageNumber;
    }

    public Pagination setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public Integer getPageSize() {
        if (null == pageNumber || null == pageSize) {
            return null;
        }
        pageSize = pageSize < 1 ? 10 : pageSize;
        return pageSize;
    }

    public Pagination setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
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

    public Pagination addOrderBy(String orderBy, OrderByDirection orderByDirection, Integer index) {
        ValidationUtil.INSTANCE.notBlank(orderBy, "orderBy must not be blank");
        ValidationUtil.INSTANCE.notNull(orderByDirection, "orderByDirection must not be null");
        if (null == this.orderBys) {
            this.orderBys = new ArrayList<>();
        }
        if (null == index) {
            this.orderBys.add(OrderBy.newInstance(orderBy, orderByDirection));
            return this;
        }
        this.orderBys.add(index, OrderBy.newInstance(orderBy, orderByDirection));
        return this;
    }

    public Pagination addOrderBy(String orderby, OrderByDirection direction) {
        addOrderBy(orderby, direction, null);
        return this;
    }

    public Pagination addOrderBys(List<OrderBy> orderBys) {
        ValidationUtil.INSTANCE.notEmpty(orderBys, "orderBys must not be empty");
        if (null == this.orderBys) {
            this.orderBys = new ArrayList<>();
        }
        this.orderBys.addAll(orderBys);
        return this;
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
