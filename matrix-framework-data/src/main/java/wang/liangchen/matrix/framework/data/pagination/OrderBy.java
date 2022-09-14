package wang.liangchen.matrix.framework.data.pagination;


import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

/**
 * @author LiangChen.Wang
 */
public final class OrderBy {
    private final String orderBy;
    private final String direction;

    public static OrderBy newInstance(String orderby, OrderByDirection direction) {
        return new OrderBy(orderby, direction);
    }

    public OrderBy(String orderBy) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,orderBy, "Parameter 'orderBy' cannot be blank");
        this.orderBy = orderBy;
        this.direction = OrderByDirection.asc.name();
    }

    public OrderBy(String orderby, OrderByDirection direction) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,orderby, "Parameter 'orderBy' cannot be blank");
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN,direction, "Parameter 'direction' cannot be blank");
        this.orderBy = orderby;
        this.direction = direction.name();
    }

    public String getOrderBy() {
        return orderBy;
    }

    public String getDirection() {
        return direction;
    }

}
