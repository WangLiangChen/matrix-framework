package wang.liangchen.matrix.shop.product.domain.aggregate.brand;

import wang.liangchen.matrix.shop.product.domain.shared.DomainException;

import java.util.Objects;

/**
 * Platform-owned brand identity. A brand is not a manufacturer or seller.
 */
public final class Brand {
    public enum Status {ENABLED, DISABLED}

    private final BrandId id;
    private final String code;
    private String name;
    private String description;
    private String logoMediaId;
    private String website;
    private String countryCode;
    private Status status;
    private long version;

    private Brand(BrandId id, String code, String name, String description, String logoMediaId, String website, String countryCode) {
        this.id = Objects.requireNonNull(id);
        this.code = require(code);
        this.name = require(name);
        this.description = description;
        this.logoMediaId = logoMediaId;
        this.website = website;
        this.countryCode = countryCode;
        this.status = Status.ENABLED;
    }

    public static Brand create(String code, String name, String description, String logoMediaId, String website, String countryCode) {
        return create(BrandId.newId(), code, name, description, logoMediaId, website, countryCode);
    }

    public static Brand create(BrandId id, String code, String name, String description, String logoMediaId, String website, String countryCode) {
        return new Brand(id, code, name, description, logoMediaId, website, countryCode);
    }

    public void rename(String name, String description) {
        this.name = require(name);
        this.description = description;
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

    public void assertUsable() {
        if (status != Status.ENABLED) throw new DomainException("Brand is disabled: " + code);
    }

    public BrandId id() {
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

    public String logoMediaId() {
        return logoMediaId;
    }

    public String website() {
        return website;
    }

    public String countryCode() {
        return countryCode;
    }

    public Status status() {
        return status;
    }

    public long version() {
        return version;
    }

    private static String require(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("value required");
        return v;
    }
}
