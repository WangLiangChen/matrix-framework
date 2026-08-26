package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * SKU持久化对象：仅存在于南向适配层，由仓储适配器与领域模型相互翻译。
 */
@Entity
@Table(name = "sku")
public class SkuPo {

    @Id
    @Column(name = "id")
    private String id;

    @ElementCollection
    @CollectionTable(name = "sku_attribute_value", joinColumns = @JoinColumn(name = "sku_id"))
    private List<AttributeValueRefPo> attributeValues = new ArrayList<>();

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "currency", nullable = false, length = 8)
    private String currency;

    @Column(name = "stock", nullable = false)
    private int stock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AttributeValueRefPo> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<AttributeValueRefPo> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
