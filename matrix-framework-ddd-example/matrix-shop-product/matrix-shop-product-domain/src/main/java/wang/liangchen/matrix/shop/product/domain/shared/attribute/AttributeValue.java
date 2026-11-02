package wang.liangchen.matrix.shop.product.domain.shared.attribute;

import wang.liangchen.matrix.shop.product.domain.aggregate.attribute.AttributeId;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Typed dynamic value. The domain never stores every attribute as an untyped String. */
public sealed interface AttributeValue permits AttributeValue.Text, AttributeValue.IntegerNumber,
        AttributeValue.DecimalNumber, AttributeValue.Flag, AttributeValue.DateValue,
        AttributeValue.DateTimeValue, AttributeValue.EnumOption, AttributeValue.EnumOptions,
        AttributeValue.Measure {
    AttributeId attributeId();
    AttributeDataType dataType();

    record Text(AttributeId attributeId, String value, AttributeDataType dataType) implements AttributeValue {
        public Text {
            Objects.requireNonNull(attributeId); Objects.requireNonNull(value); Objects.requireNonNull(dataType);
            if (dataType != AttributeDataType.TEXT && dataType != AttributeDataType.LONG_TEXT)
                throw new IllegalArgumentException("Text value requires TEXT or LONG_TEXT data type");
        }
    }
    record IntegerNumber(AttributeId attributeId, long value) implements AttributeValue {
        public IntegerNumber { Objects.requireNonNull(attributeId); }
        public AttributeDataType dataType() { return AttributeDataType.INTEGER; }
    }
    record DecimalNumber(AttributeId attributeId, BigDecimal value) implements AttributeValue {
        public DecimalNumber { Objects.requireNonNull(attributeId); Objects.requireNonNull(value); }
        public AttributeDataType dataType() { return AttributeDataType.DECIMAL; }
    }
    record Flag(AttributeId attributeId, boolean value) implements AttributeValue {
        public Flag { Objects.requireNonNull(attributeId); }
        public AttributeDataType dataType() { return AttributeDataType.BOOLEAN; }
    }
    record DateValue(AttributeId attributeId, LocalDate value) implements AttributeValue {
        public DateValue { Objects.requireNonNull(attributeId); Objects.requireNonNull(value); }
        public AttributeDataType dataType() { return AttributeDataType.DATE; }
    }
    record DateTimeValue(AttributeId attributeId, Instant value) implements AttributeValue {
        public DateTimeValue { Objects.requireNonNull(attributeId); Objects.requireNonNull(value); }
        public AttributeDataType dataType() { return AttributeDataType.DATETIME; }
    }
    record EnumOption(AttributeId attributeId, String optionCode) implements AttributeValue {
        public EnumOption { Objects.requireNonNull(attributeId); require(optionCode, "optionCode"); }
        public AttributeDataType dataType() { return AttributeDataType.ENUM; }
    }
    record EnumOptions(AttributeId attributeId, List<String> optionCodes) implements AttributeValue {
        public EnumOptions {
            Objects.requireNonNull(attributeId); optionCodes = List.copyOf(optionCodes);
            if (optionCodes.isEmpty()) throw new IllegalArgumentException("Enum set cannot be empty");
        }
        public AttributeDataType dataType() { return AttributeDataType.ENUM_SET; }
    }
    record Measure(AttributeId attributeId, BigDecimal value, String unitCode) implements AttributeValue {
        public Measure {
            Objects.requireNonNull(attributeId); Objects.requireNonNull(value); require(unitCode, "unitCode");
        }
        public AttributeDataType dataType() { return AttributeDataType.MEASURE; }
    }
    private static void require(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
    }
}
