package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性持久化对象：仅存在于南向适配层，由仓储适配器与领域模型相互翻译。
 */
@Entity
@Table(name = "attribute")
public class AttributePo {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false, length = 16)
    private String type;

    @ElementCollection
    @CollectionTable(name = "attribute_option", joinColumns = @JoinColumn(name = "attribute_id"))
    @Column(name = "option", nullable = false)
    private List<String> options = new ArrayList<>();

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
