package wang.liangchen.matrix.shop.product.domain.attribute;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性聚合根：商品的属性定义及其可选值，
 * 商品聚合通过属性标识(AttributeId)引用属性并携带属性值。
 */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class Attribute extends AbstractAggregateRoot<AttributeId> {

    @Identity
    private final AttributeId id;
    private String name;
    private AttributeType type;
    private final List<String> options;

    Attribute(AttributeId id, String name, AttributeType type, List<String> options) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.options = new ArrayList<>(options);
    }

    public AttributeId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public AttributeType type() {
        return type;
    }

    public List<String> options() {
        return List.copyOf(options);
    }

    /**
     * 收集属性创建领域事实：仅由属性工厂在创建新属性时调用
     * （AbstractAggregateRoot#raise为受保护成员，事件由聚合自身收集）。
     */
    void created() {
        raise(new AttributeCreatedEvent(id, name, type));
    }

    /**
     * 重命名属性。
     */
    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new DomainException("属性名称不能为空");
        }
        this.name = newName;
    }

    /**
     * 变更属性类型。
     */
    public void changeType(AttributeType newType) {
        if (newType == null) {
            throw new DomainException("属性类型不能为空");
        }
        this.type = newType;
    }

    /**
     * 增加属性选项，同一属性的选项不得重复。
     */
    public void addOption(String option) {
        if (option == null || option.isBlank()) {
            throw new DomainException("属性选项不能为空");
        }
        if (options.contains(option)) {
            throw new DomainException("属性选项已存在");
        }
        options.add(option);
    }

    /**
     * 移除属性选项。
     */
    public void removeOption(String option) {
        if (!options.remove(option)) {
            throw new DomainException("属性选项不存在");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Attribute that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}