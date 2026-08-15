package wang.liangchen.matrix.framework.ddd.northbound.exception;

/**
 * 应用层异常：应用服务捕获领域异常(DomainException)或基础设施技术异常后包装抛出，
 * 消息中可附加用例信息；领域层不得依赖本异常。
 *
 * @author Liangchen.Wang
 */
public abstract class AbstractApplicationException extends RuntimeException implements IApplicationException {
    public AbstractApplicationException(String message) {
        super(message);
    }

    public AbstractApplicationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
