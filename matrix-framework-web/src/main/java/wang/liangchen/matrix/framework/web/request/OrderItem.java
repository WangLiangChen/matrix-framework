package wang.liangchen.matrix.framework.web.request;

/**
 * @author Liangchen.Wang 2022-09-16 15:03
 */
class OrderItem {
    private String orderBy;
    private String direction;

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
