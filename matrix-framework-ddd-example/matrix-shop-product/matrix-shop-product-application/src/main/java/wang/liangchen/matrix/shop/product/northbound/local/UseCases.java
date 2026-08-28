package wang.liangchen.matrix.shop.product.northbound.local;

import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.function.Supplier;

/**
 * 用例执行助手：捕获领域异常并包装为应用异常，附加用例上下文，
 * 供商品上下文各应用服务复用的横切关注点。
 */
final class UseCases {

    private UseCases() {
    }

    static <T> T execute(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
