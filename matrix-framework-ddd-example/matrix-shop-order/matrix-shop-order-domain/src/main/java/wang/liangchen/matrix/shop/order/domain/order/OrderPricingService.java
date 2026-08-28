package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.service.IDomainService;
import wang.liangchen.matrix.shop.order.domain.shared.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 订单定价领域服务：封装不适合放在订单聚合内的定价计算——
 * 大宗折扣（单一商品购买数量达到门槛时该项单价享折扣）与
 * 忠诚折扣（金卡买家对折后总额再享折扣），为无状态的领域概念。
 */
@DomainModel(DomainMetaModel.DomainService)
public class OrderPricingService implements IDomainService {

    /** 大宗折扣的购买数量门槛（同一商品）。 */
    public static final int BULK_QUANTITY_THRESHOLD = 10;
    /** 大宗折扣的单价折扣率。 */
    public static final BigDecimal BULK_DISCOUNT_RATE = new BigDecimal("0.95");
    /** 忠诚折扣的总额折扣率（金卡买家）。 */
    public static final BigDecimal LOYALTY_DISCOUNT_RATE = new BigDecimal("0.98");

    /**
     * 计算订单总额：先按订单项汇总（大宗项按折扣单价计），金卡买家再享忠诚折扣。
     */
    public Money totalOf(List<OrderItemTemplate> itemTemplates, LoyaltyLevel loyalty) {
        Money subtotal = itemTemplates.stream()
                .map(this::lineAmount)
                .reduce(Money.ZERO, Money::add);
        return applyLoyaltyDiscount(subtotal, loyalty);
    }

    /**
     * 订单项金额：购买数量达到大宗门槛时按折扣单价计。
     */
    private Money lineAmount(OrderItemTemplate template) {
        Money unitPrice = template.unitPrice();
        if (template.quantity() >= BULK_QUANTITY_THRESHOLD) {
            unitPrice = discounted(unitPrice, BULK_DISCOUNT_RATE);
        }
        return unitPrice.multiply(template.quantity());
    }

    private Money applyLoyaltyDiscount(Money amount, LoyaltyLevel loyalty) {
        return loyalty == LoyaltyLevel.GOLD ? discounted(amount, LOYALTY_DISCOUNT_RATE) : amount;
    }

    private Money discounted(Money price, BigDecimal rate) {
        return new Money(price.amount().multiply(rate).setScale(2, RoundingMode.HALF_UP), price.currency());
    }
}
