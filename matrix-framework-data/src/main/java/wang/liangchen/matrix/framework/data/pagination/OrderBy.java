package wang.liangchen.matrix.framework.data.pagination;


import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * @author LiangChen.Wang
 */
public final class OrderBy implements Serializable {
    private String orderBy;
    private OrderByDirection direction;

    public OrderBy() {

    }

    public static OrderBy newInstance(String orderBy, OrderByDirection direction) {
        return new OrderBy(orderBy, direction);
    }

    public OrderBy(String orderBy, OrderByDirection direction) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN, orderBy, "Parameter 'orderBy' cannot be blank");
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, direction, "Parameter 'direction' cannot be null");
        this.orderBy = orderBy;
        this.direction = direction;
    }

    public OrderBy(String orderBy) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN, orderBy, "Parameter 'orderBy' cannot be blank");
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

    @Override
    public String toString() {
        return new StringJoiner(", ", "OrderBy[", "]")
                .add("orderBy='" + orderBy + "'")
                .add("direction='" + direction + "'")
                .toString();
    }
}
