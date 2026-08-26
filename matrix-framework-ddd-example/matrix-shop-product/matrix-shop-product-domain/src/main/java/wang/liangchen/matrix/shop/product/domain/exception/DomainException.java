package wang.liangchen.matrix.shop.product.domain.exception;

import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;

/**
 * 领域异常：领域层业务规则违反时抛出，消息使用统一语言描述业务含义。
 */
public class DomainException extends AbstractDomainException {

    public DomainException() {
    }

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainException(Throwable cause) {
        super(cause);
    }
}
