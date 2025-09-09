package wang.liangchen.matrix.framework.spring.boot.aop;

import org.springframework.beans.factory.Aware;

/**
 * @author Liangchen.Wang
 * Interface to be implemented by beans that wish to be aware of their owning proxy object
 */
public interface ProxyObjectAware extends Aware {
    /**
     *
     * @param proxyObject the proxy object of target object
     */
    void setProxyObject(Object proxyObject);
}
