package wang.liangchen.matrix.framework.spring.boot.aop;

import org.springframework.beans.factory.Aware;

/**
 * @author Liangchen.Wang
 */
public interface ProxyObjectAware extends Aware {
    /**
     * Inject the proxy object into the proxy class instance that implements this interface
     *
     * @param proxyObject the proxy object of target object
     */
    void setProxyObject(Object proxyObject);
}
