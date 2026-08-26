package wang.liangchen.matrix.shop.order.message.response;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IView;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车视图：购物车查询视图。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.VIEW, exchangePattern = MessageExchangePattern.RequestResponse)
public record CartView(String id, String buyerId, List<CartItemView> items, BigDecimal totalAmount) implements IView {
}
