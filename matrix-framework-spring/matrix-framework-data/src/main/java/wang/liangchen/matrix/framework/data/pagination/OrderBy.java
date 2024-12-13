package wang.liangchen.matrix.framework.data.pagination;


import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;

/**
 * @author LiangChen.Wang
 */
public final class OrderBy implements Serializable {
    private String orderBy;
    private OrderByDirection direction;

    public static OrderBy newInstance() {
        return ClassUtil.INSTANCE.instantiate(OrderBy.class);
    }

    public static OrderBy newInstance(String orderBy, OrderByDirection direction) {
        return new OrderBy(orderBy, direction);
    }

    public OrderBy(String orderBy, OrderByDirection direction) {
        ValidationUtil.INSTANCE.notNullAndBlank(orderBy, "Parameter 'orderBy' cannot be blank");
        ValidationUtil.INSTANCE.notNull(direction, "Parameter 'direction' cannot be null");
        this.orderBy = orderBy;
        this.direction = direction;
    }

    public OrderBy(String orderBy) {
        ValidationUtil.INSTANCE.notNullAndBlank(orderBy, "Parameter 'orderBy' cannot be blank");
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public OrderBy setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public OrderByDirection getDirection() {
        return direction;
    }

    public OrderBy setDirection(OrderByDirection direction) {
        this.direction = direction;
        return this;
    }
}
