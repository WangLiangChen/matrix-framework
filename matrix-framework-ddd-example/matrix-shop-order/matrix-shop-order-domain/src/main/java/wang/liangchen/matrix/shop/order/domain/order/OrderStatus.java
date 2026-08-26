package wang.liangchen.matrix.shop.order.domain.order;

/**
 * 订单状态：订单的生命周期状态机。
 */
public enum OrderStatus {
    Created("待支付"),
    Paid("已支付"),
    Shipped("已发货"),
    Completed("已完成"),
    Canceled("已取消");

    private final String summary;

    OrderStatus(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }
}
