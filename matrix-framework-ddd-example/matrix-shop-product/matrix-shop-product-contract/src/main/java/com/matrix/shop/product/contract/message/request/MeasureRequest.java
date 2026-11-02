package wang.liangchen.matrix.product.contract.message.request;
import java.math.BigDecimal;
public record MeasureRequest(BigDecimal value, String unitCode) {}
