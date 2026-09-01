package wang.liangchen.matrix.framework.ddd.domain.identity;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;

import java.util.Objects;
import java.util.UUID;

/**
 * 通用UUID身份标识：系统生成、无业务含义的代理标识。
 * 不变类：final类、值在构造时确定，通过静态工厂of/next创建；值相等语义继承自AbstractValueObject。
 *
 * @author Liangchen.Wang
 */
@DomainModel(DomainMetaModel.Identity)
public final class UUIDIdentity extends AbstractSimpleIdentity<String> implements IStringIdentity {

    private final String value;

    private UUIDIdentity(String value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * 将身份标识的值转换为身份标识对象；值必须为合法UUID字符串（不符合则抛出IllegalArgumentException）。
     */
    public static UUIDIdentity of(String value) {
        Objects.requireNonNull(value, "value must not be null");
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("value must be a valid UUID string: " + value, e);
        }
        return new UUIDIdentity(value);
    }

    /**
     * 生成新的随机身份标识。
     */
    public static UUIDIdentity next() {
        return new UUIDIdentity(UUID.randomUUID().toString());
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
