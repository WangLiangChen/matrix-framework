package wang.liangchen.matrix.shop.order.southbound.adapter.repository;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车持久化对象：以购物车为根、购物车项为子实体的持久化对象图，
 * 仅存在于南向适配层。
 */
@Entity
@Table(name = "cart")
public class CartPo {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id", nullable = false)
    private List<CartItemPo> items = new ArrayList<>();

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

    public List<CartItemPo> getItems() {
        return items;
    }

    public void setItems(List<CartItemPo> items) {
        this.items = items;
    }
}
