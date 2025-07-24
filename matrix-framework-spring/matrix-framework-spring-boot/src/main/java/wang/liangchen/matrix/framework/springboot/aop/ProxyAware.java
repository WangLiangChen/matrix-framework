package wang.liangchen.matrix.framework.springboot.aop;

import org.springframework.beans.factory.Aware;

/**
 * @author Liangchen.Wang
 */
public interface ProxyAware extends Aware {
    /**
     * 实现此接口的类，可以注入当前类实例的代理对象，用于解决AOP注解失效的问题
     * @param proxy
     */
    void setProxy(Object proxy);
}
