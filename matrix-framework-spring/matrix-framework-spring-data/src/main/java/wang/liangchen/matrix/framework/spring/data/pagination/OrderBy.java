package wang.liangchen.matrix.framework.spring.data.pagination;


import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author LiangChen.Wang
 */
public final class OrderBy implements Serializable {
    private String orderBy;
    private OrderByDirection direction;

    public OrderBy() {
    }

    public OrderBy(String orderBy, OrderByDirection direction) {
        ValidationUtil.INSTANCE.notNullAndBlank(orderBy, "The parameter 'orderBy' cannot be blank");
        this.orderBy = orderBy;
        this.direction = direction;
    }

    public OrderBy(String orderBy) {
        this(orderBy, OrderByDirection.asc);
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public OrderByDirection getDirection() {
        return direction;
    }

    public void setDirection(OrderByDirection direction) {
        this.direction = direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderBy, direction);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        OrderBy that = (OrderBy) object;
        return Objects.equals(orderBy, that.orderBy) && direction == that.direction;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "OrderBy[", "]")
                .add("orderBy='" + orderBy + "'")
                .add("direction=" + direction)
                .toString();
    }
}
