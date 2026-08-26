package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品持久化对象：以SPU为根、SKU为子实体的持久化对象图，
 * 仅存在于南向适配层，由仓储适配器与领域模型相互翻译。
 */
@Entity
@Table(name = "product")
public class ProductPo {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @Column(name = "brand_id", nullable = false)
    private String brandId;

    @Column(name = "listed", nullable = false)
    private boolean listed;

    @ElementCollection
    @CollectionTable(name = "product_attribute_value", joinColumns = @JoinColumn(name = "product_id"))
    private List<AttributeValueRefPo> attributeValues = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id", nullable = false)
    private List<SkuPo> skus = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public boolean isListed() {
        return listed;
    }

    public void setListed(boolean listed) {
        this.listed = listed;
    }

    public List<AttributeValueRefPo> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<AttributeValueRefPo> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public List<SkuPo> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuPo> skus) {
        this.skus = skus;
    }
}
