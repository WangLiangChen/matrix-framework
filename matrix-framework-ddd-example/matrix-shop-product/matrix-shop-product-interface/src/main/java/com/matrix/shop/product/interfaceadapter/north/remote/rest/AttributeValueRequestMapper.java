package wang.liangchen.matrix.product.interfaceadapter.north.remote.rest;

import wang.liangchen.matrix.product.contract.message.request.AttributeValueRequest;
import wang.liangchen.matrix.product.domain.shared.attribute.*;
import wang.liangchen.matrix.shop.product.domain.aggregate.attribute.AttributeId;
import java.math.BigDecimal; import java.time.*; import java.util.*;

final class AttributeValueRequestMapper {
    private AttributeValueRequestMapper() {}
    static AttributeValue toDomain(AttributeValueRequest r) {
        AttributeId id = new AttributeId(Objects.requireNonNull(r.attributeId(), "attributeId"));
        AttributeDataType type = AttributeDataType.valueOf(r.dataType());
        return switch (type) {
            case TEXT, LONG_TEXT -> new AttributeValue.Text(id, Objects.requireNonNull(r.value()), type);
            case INTEGER -> new AttributeValue.IntegerNumber(id, Long.parseLong(r.value()));
            case DECIMAL -> new AttributeValue.DecimalNumber(id, new BigDecimal(r.value()));
            case BOOLEAN -> new AttributeValue.Flag(id, Boolean.parseBoolean(r.value()));
            case DATE -> new AttributeValue.DateValue(id, LocalDate.parse(r.value()));
            case DATETIME -> new AttributeValue.DateTimeValue(id, Instant.parse(r.value()));
            case ENUM -> new AttributeValue.EnumOption(id, Objects.requireNonNull(r.value()));
            case ENUM_SET -> new AttributeValue.EnumOptions(id, r.values());
            case MEASURE -> new AttributeValue.Measure(id, new BigDecimal(r.value()), r.unitCode());
        };
    }
}
