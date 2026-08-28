package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.shop.order.domain.exception.DomainException;

/**
 * 忠诚等级：买家在订单上下文中的会员等级，下单定价时作为忠诚折扣依据。
 */
public enum LoyaltyLevel {
    REGULAR("普通"),
    GOLD("金卡");

    private final String summary;

    LoyaltyLevel(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public static LoyaltyLevel of(String value) {
        return switch (value == null ? "" : value.trim().toUpperCase()) {
            case "GOLD" -> GOLD;
            case "REGULAR", "" -> REGULAR;
            default -> throw new DomainException("未知忠诚等级：" + value);
        };
    }
}
