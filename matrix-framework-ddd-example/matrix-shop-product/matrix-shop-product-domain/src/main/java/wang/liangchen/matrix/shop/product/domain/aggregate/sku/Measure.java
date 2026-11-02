package wang.liangchen.matrix.shop.product.domain.aggregate.sku;
import java.math.BigDecimal;
public record Measure(BigDecimal value, String unitCode) {
    public Measure { if (value == null || value.signum() < 0) throw new IllegalArgumentException("measure must be non-negative"); if (unitCode == null || unitCode.isBlank()) throw new IllegalArgumentException("unitCode required"); }
}
