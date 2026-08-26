package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;

/**
 * 收货地址：订单的收货人信息，按属性值组合定义相等性。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record Address(String receiver, String phone, String province, String city, String detail) implements IValueObject {

    public Address {
        if (receiver == null || receiver.isBlank()) {
            throw new DomainException("收货人不能为空");
        }
        if (detail == null || detail.isBlank()) {
            throw new DomainException("收货地址不能为空");
        }
    }
}
