package wang.liangchen.matrix.shop.product.northbound.exception;

import wang.liangchen.matrix.framework.ddd.northbound.exception.AbstractApplicationException;

/**
 * 应用异常：应用服务捕获领域异常后包装抛出，附加用例上下文信息。
 */
public class ApplicationException extends AbstractApplicationException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
