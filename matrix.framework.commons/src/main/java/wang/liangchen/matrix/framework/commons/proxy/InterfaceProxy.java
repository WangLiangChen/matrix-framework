package wang.liangchen.matrix.framework.commons.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author LiangChen.Wang 2019/9/29 13:57
 */
public class InterfaceProxy {
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> interfaceClass) {
        final Handler<T> handler = new Handler<>(interfaceClass);
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[]{interfaceClass}, handler);
    }

    static class Handler<T> implements InvocationHandler {
        private final Class<T> interfaceClass;

        public Handler(Class<T> interfaceClass) {
            this.interfaceClass = interfaceClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)   {
            //在这里写方法实现
            return null;
        }
    }

    static class CglibProxy implements MethodInterceptor {
        private final Enhancer enhancer = new Enhancer();

        public Object getProxy(Class clazz) {
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(this);
            return enhancer.create();
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("前置代理");
            Object result = proxy.invokeSuper(obj, args);
            System.out.println("后置代理");
            return result;
        }
    }
}
