package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * 属性值引用持久化对象：仅存在于南向适配层，不向领域层泄漏。
 */
@Embeddable
public class AttributeValueRefPo {

    @Column(name = "attribute_id", nullable = false)
    private String attributeId;

    @Column(name = "attribute_value", nullable = false)
    private String value;

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttributeValueRefPo that)) {
            return false;
        }
        return Objects.equals(attributeId, that.attributeId) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeId, value);
    }
}
