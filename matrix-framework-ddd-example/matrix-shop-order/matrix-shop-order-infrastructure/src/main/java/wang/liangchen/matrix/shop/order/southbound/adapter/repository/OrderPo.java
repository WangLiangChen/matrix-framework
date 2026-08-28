package wang.liangchen.matrix.shop.order.southbound.adapter.repository;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单持久化对象：以订单为根、订单项为子实体的持久化对象图，
 * 收货地址平铺为订单列，仅存在于南向适配层。
 */
@Entity
@Table(name = "orders")
public class OrderPo {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "receiver_province")
    private String receiverProvince;

    @Column(name = "receiver_city")
    private String receiverCity;

    @Column(name = "receiver_detail", nullable = false)
    private String receiverDetail;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @Column(name = "placed_on", nullable = false)
    private Instant placedOn;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private java.math.BigDecimal totalAmount;

    @Column(name = "currency", nullable = false, length = 8)
    private String currency;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderItemPo> items = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverDetail() {
        return receiverDetail;
    }

    public void setReceiverDetail(String receiverDetail) {
        this.receiverDetail = receiverDetail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getPlacedOn() {
        return placedOn;
    }

    public void setPlacedOn(Instant placedOn) {
        this.placedOn = placedOn;
    }

    public java.math.BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(java.math.BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<OrderItemPo> getItems() {
        return items;
    }

    public void setItems(List<OrderItemPo> items) {
        this.items = items;
    }
}
