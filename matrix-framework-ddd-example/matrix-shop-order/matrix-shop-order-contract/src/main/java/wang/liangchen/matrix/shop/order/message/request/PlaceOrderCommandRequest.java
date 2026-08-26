package wang.liangchen.matrix.shop.order.message.request;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;
import wang.liangchen.matrix.framework.ddd.contract.request.IRequest;

import java.util.List;

/**
 * 下单命令请求：下单时仅携带商品标识与数量，商品名称与单价由应用服务
 * 通过商品客户端端口获取后快照。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.COMMAND_REQUEST, exchangePattern = MessageExchangePattern.FireAndForget)
public record PlaceOrderCommandRequest(String buyerId, Receiver receiver, List<Item> items) implements ICommandRequest {

    /**
     * 收货人信息。
     */
    @MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.REQUEST, exchangePattern = MessageExchangePattern.RequestResponse)
    public record Receiver(String receiver, String phone, String province, String city, String detail) implements IRequest {
    }

    /**
     * 订单商品项。
     */
    @MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.REQUEST, exchangePattern = MessageExchangePattern.RequestResponse)
    public record Item(String productId, int quantity) implements IRequest {
    }
}
