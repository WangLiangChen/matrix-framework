package wang.liangchen.matrix.framework.spring.boot.context;

import wang.liangchen.matrix.framework.commons.function.*;
import wang.liangchen.matrix.framework.commons.function.consumer.SerializableBiConsumer;
import wang.liangchen.matrix.framework.commons.function.consumer.SerializableConsumer;
import wang.liangchen.matrix.framework.commons.function.consumer.SerializableQuaConsumer;
import wang.liangchen.matrix.framework.commons.function.consumer.SerializableTriConsumer;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;

import java.lang.invoke.SerializedLambda;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum SpringFunctionalExecutor {
    INSTANCE;
    private static final Map<String, Object> cache = new ConcurrentHashMap<>();

    public <T, R> R execute(SerializableFunction<T, R> function) {
        T t = resolveFunction(function);
        return function.apply(t);
    }

    public <T> void execute(SerializableConsumer<T> function) {
        T t = resolveFunction(function);
        function.accept(t);
    }

    public <T, U, R> R execute(SerializableBiFunction<T, U, R> function, U u) {
        T t = resolveFunction(function);
        return function.apply(t, u);
    }

    public <T, U> void execute(SerializableBiConsumer<T, U> function, U u) {
        T t = resolveFunction(function);
        function.accept(t, u);
    }


    public <T, U, V, R> R execute(SerializableTriFunction<T, U, V, R> function, U u, V v) {
        T t = resolveFunction(function);
        return function.apply(t, u, v);
    }

    public <T, U, V> void execute(SerializableTriConsumer<T, U, V> function, U u, V v) {
        T t = resolveFunction(function);
        function.accept(t, u, v);
    }

    public <T, U, V, W, R> R execute(SerializableQuaFunction<T, U, V, W, R> function, U u, V v, W w) {
        T t = resolveFunction(function);
        return function.apply(t, u, v, w);
    }

    public <T, U, V, W> void execute(SerializableQuaConsumer<T, U, V, W> function, U u, V v, W w) {
        T t = resolveFunction(function);
        function.accept(t, u, v, w);
    }

    private <T> T resolveFunction(SerializableFunctionalInterface<T> function) {
        SerializedLambda lambda = LambdaUtil.INSTANCE.serializedLambda(function);
        if (lambda.getCapturedArgCount() > 0) {
            throw new IllegalArgumentException("The function must be a class method.");
        }
        String className = lambda.getImplClass();
        Object cachedBean = cache.computeIfAbsent(className, key -> {
            Class<T> clazz = ClassUtil.INSTANCE.forName(className.replace('/', '.'));
            return BeanContext.INSTANCE.getBean(clazz);
        });
        return ObjectUtil.INSTANCE.cast(cachedBean);
    }
}
