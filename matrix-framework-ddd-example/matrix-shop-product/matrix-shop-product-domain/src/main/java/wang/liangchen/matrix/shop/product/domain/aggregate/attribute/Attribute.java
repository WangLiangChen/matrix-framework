package wang.liangchen.matrix.shop.product.domain.aggregate.attribute;

import wang.liangchen.matrix.shop.product.domain.shared.DomainException;
import wang.liangchen.matrix.shop.product.domain.shared.attribute.AttributeDataType;

import java.util.*;

/**
 * Platform-owned reusable attribute definition; concrete values belong to SPU or SKU.
 */
public final class Attribute {
    public enum Status {ENABLED, DISABLED}

    public enum ValueMode {FREE_TEXT, ENUM_OPTIONS, DICTIONARY_REFERENCE}

    public enum Cardinality {SINGLE, MULTIPLE}

    private final AttributeId id;
    private final String code;
    private String name;
    private String description;
    private final AttributeDataType dataType;
    private final ValueMode valueMode;
    private final Cardinality cardinality;
    private final String unitCode;
    private final boolean localized;
    private List<AttributeOption> options;
    private Status status;
    private long version;

    private Attribute(AttributeId id, String code, String name, String description, AttributeDataType dataType,
                      ValueMode valueMode, Cardinality cardinality, String unitCode, boolean localized,
                      List<AttributeOption> options) {
        this.id = Objects.requireNonNull(id);
        this.code = require(code, "code");
        this.name = require(name, "name");
        this.description = description;
        this.dataType = Objects.requireNonNull(dataType);
        this.valueMode = Objects.requireNonNull(valueMode);
        this.cardinality = Objects.requireNonNull(cardinality);
        this.unitCode = unitCode;
        this.localized = localized;
        this.options = List.copyOf(options);
        this.status = Status.ENABLED;
        this.version = 0;
        validateDefinition();
    }

    public static Attribute create(String code, String name, String description, AttributeDataType dataType,
                                   ValueMode valueMode, Cardinality cardinality, String unitCode,
                                   boolean localized, List<AttributeOption> options) {
        return create(AttributeId.newId(), code, name, description, dataType, valueMode, cardinality, unitCode, localized, options);
    }

    public static Attribute create(AttributeId id, String code, String name, String description, AttributeDataType dataType,
                                   ValueMode valueMode, Cardinality cardinality, String unitCode,
                                   boolean localized, List<AttributeOption> options) {
        return new Attribute(id, code, name, description, dataType, valueMode, cardinality, unitCode, localized, options);
    }

    private void validateDefinition() {
        if ((dataType == AttributeDataType.ENUM || dataType == AttributeDataType.ENUM_SET) != (valueMode == ValueMode.ENUM_OPTIONS))
            throw new DomainException("ENUM/ENUM_SET must use ENUM_OPTIONS and other types must not");
        if ((dataType == AttributeDataType.MEASURE) != (unitCode != null && !unitCode.isBlank()))
            throw new DomainException("MEASURE requires a default unitCode; other data types must not declare it");
        if (dataType == AttributeDataType.ENUM && cardinality != Cardinality.SINGLE)
            throw new DomainException("ENUM cardinality must be SINGLE");
        if (dataType == AttributeDataType.ENUM_SET && cardinality != Cardinality.MULTIPLE)
            throw new DomainException("ENUM_SET cardinality must be MULTIPLE");
        if (dataType == AttributeDataType.ENUM || dataType == AttributeDataType.ENUM_SET) {
            if (options.isEmpty()) throw new DomainException("Enum attribute must have options");
            Set<String> codes = new HashSet<>();
            for (AttributeOption option : options)
                if (!codes.add(option.code())) throw new DomainException("Duplicate option code: " + option.code());
        } else if (!options.isEmpty()) throw new DomainException("Non-enum attribute cannot have options");
    }

    public void rename(String name, String description) {
        this.name = require(name, "name");
        this.description = description;
        version++;
    }

    public void replaceOptions(List<AttributeOption> options) {
        if (dataType != AttributeDataType.ENUM && dataType != AttributeDataType.ENUM_SET)
            throw new DomainException("Only enum attributes have options");
        List<AttributeOption> previous = this.options;
        this.options = List.copyOf(options);
        validateDefinition();
        Set<String> next = new HashSet<>();
        this.options.forEach(o -> next.add(o.code()));
        for (AttributeOption old : previous)
            if (!next.contains(old.code()))
                throw new DomainException("Option codes are stable; deactivate an option instead of removing it: " + old.code());
        version++;
    }

    public void disable() {
        status = Status.DISABLED;
        version++;
    }

    public void enable() {
        status = Status.ENABLED;
        version++;
    }

    public AttributeId id() {
        return id;
    }

    public String code() {
        return code;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public AttributeDataType dataType() {
        return dataType;
    }

    public ValueMode valueMode() {
        return valueMode;
    }

    public Cardinality cardinality() {
        return cardinality;
    }

    public String unitCode() {
        return unitCode;
    }

    public boolean localized() {
        return localized;
    }

    public List<AttributeOption> options() {
        return options;
    }

    public Status status() {
        return status;
    }

    public long version() {
        return version;
    }

    private static String require(String v, String n) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(n + " required");
        return v;
    }
}
