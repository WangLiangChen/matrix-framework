package wang.liangchen.matrix.framework.spring.boot.reflect;


import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class DelegatingConcurrentMethodInterceptor implements MethodInterceptor {
    private final Object delegate;

    private DelegatingConcurrentMethodInterceptor(Object delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType.isAssignableFrom(Runnable.class)) {
                Runnable runnable = (Runnable) args[i];
                args[i] = TtlRunnable.get(runnable);
                continue;
            }
            if (parameterType.isAssignableFrom(Callable.class)) {
                Callable<?> callable = (Callable<?>) args[i];
                args[i] = TtlCallable.get(callable);
            }
        }
        return proxy.invoke(delegate, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target) {
        if (null == target) {
            return null;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new DelegatingConcurrentMethodInterceptor(target));
        return (T) enhancer.create();
    }
}
