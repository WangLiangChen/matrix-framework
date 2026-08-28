package wang.liangchen.matrix.shop.order.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.shop.order.domain.shared.Money;
import wang.liangchen.matrix.shop.order.domain.shared.ProductId;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 订单定价领域服务单元测试：验证大宗折扣与忠诚折扣的定价规则，
 * 不依赖Spring上下文，直接测试领域逻辑。
 */
class OrderPricingServiceTest {

    private final OrderPricingService pricingService = new OrderPricingService();

    @Test
    @DisplayName("普通买家小批量：无折扣")
    void regularBuyerWithoutDiscount() {
        Money total = pricingService.totalOf(List.of(item(2, "100.00")), LoyaltyLevel.REGULAR);
        assertThat(total.amount()).isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("大宗折扣：单一商品数量达到门槛时单价享95折")
    void bulkDiscountAtThreshold() {
        Money total = pricingService.totalOf(List.of(item(10, "100.00")), LoyaltyLevel.REGULAR);
        // 单价100.00×0.95=95.00，95.00×10=950.00
        assertThat(total.amount()).isEqualByComparingTo("950.00");
    }

    @Test
    @DisplayName("大宗折扣门槛之下不享折扣")
    void noBulkDiscountBelowThreshold() {
        Money total = pricingService.totalOf(List.of(item(9, "100.00")), LoyaltyLevel.REGULAR);
        assertThat(total.amount()).isEqualByComparingTo("900.00");
    }

    @Test
    @DisplayName("忠诚折扣：金卡买家对总额再享98折")
    void loyaltyDiscountForGoldBuyer() {
        Money total = pricingService.totalOf(List.of(item(2, "100.00")), LoyaltyLevel.GOLD);
        // 200.00×0.98=196.00
        assertThat(total.amount()).isEqualByComparingTo("196.00");
    }

    @Test
    @DisplayName("大宗折扣与忠诚折扣叠加")
    void bulkAndLoyaltyDiscountsStacked() {
        Money total = pricingService.totalOf(List.of(item(10, "100.00")), LoyaltyLevel.GOLD);
        // 950.00×0.98=931.00
        assertThat(total.amount()).isEqualByComparingTo("931.00");
    }

    @Test
    @DisplayName("多订单项按折扣规则分别汇总")
    void multipleItemsSummed() {
        Money total = pricingService.totalOf(
                List.of(item(10, "100.00"), item(1, "30.00")), LoyaltyLevel.REGULAR);
        // 950.00+30.00=980.00
        assertThat(total.amount()).isEqualByComparingTo("980.00");
    }

    private OrderItemTemplate item(int quantity, String unitPrice) {
        return new OrderItemTemplate(ProductId.of("product-1"), "测试商品",
                Money.CNY(new BigDecimal(unitPrice)), quantity);
    }
}
