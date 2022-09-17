package wang.liangchen.matrix.framework.web.request;

import java.util.List;

/**
 * @author Liangchen.Wang 2022-09-16 14:55
 */
public class PaginationRequest {
    /**
     * 分页页号
     */
    private Integer pageNumber;
    /**
     * 行数
     */
    private Integer pageSize;
    /**
     * 排序
     */
    private List<OrderItem> orderItems;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
