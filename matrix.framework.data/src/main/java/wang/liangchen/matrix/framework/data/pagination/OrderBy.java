package wang.liangchen.matrix.framework.data.pagination;


import wang.liangchen.matrix.framework.commons.exception.AssertUtil;

/**
 * @author LiangChen.Wang
 */
public final class OrderBy {
    private String orderBy;
    private String direction;

    public static OrderBy newInstance(String orderby, OrderByDirection direction) {
        return new OrderBy(orderby, direction);
    }

    public OrderBy(String orderBy) {
        AssertUtil.INSTANCE.notBlank(orderBy, "Parameter 'orderBy' cannot be blank");
        this.orderBy = orderBy;
        this.direction = OrderByDirection.asc.name();
    }

    public OrderBy(String orderby, OrderByDirection direction) {
        AssertUtil.INSTANCE.notBlank(orderby, "Parameter 'orderBy' cannot be blank");
        AssertUtil.INSTANCE.notNull(direction, "Parameter 'direction' cannot be blank");
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
