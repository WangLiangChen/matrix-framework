package wang.liangchen.matrix.framework.spring.boot.function;

import wang.liangchen.matrix.framework.commons.function.*;
import wang.liangchen.matrix.framework.commons.function.consumer.SerializableBiConsumer;
import wang.liangchen.matrix.framework.commons.function.consumer.SerializableConsumer;
import wang.liangchen.matrix.framework.commons.function.consumer.SerializableQuaConsumer;
import wang.liangchen.matrix.framework.commons.function.consumer.SerializableTriConsumer;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.spring.boot.context.BeanContext;

import java.lang.invoke.SerializedLambda;

public enum FunctionExecutor {
    INSTANCE;

    public <T, R> R execute(SerializableFunction<T, R> function) throws Throwable {
        T t = resolveFunction(function);
        return function.apply(t);
    }

    public <T> void execute(SerializableConsumer<T> function) throws Throwable {
        T t = resolveFunction(function);
        function.accept(t);
    }

    public <T, U, R> R execute(SerializableBiFunction<T, U, R> function, U u) throws Throwable {
        T t = resolveFunction(function);
        return function.apply(t, u);
    }

    public <T, U> void execute(SerializableBiConsumer<T, U> function, U u) throws Throwable {
        T t = resolveFunction(function);
        function.accept(t, u);
    }


    public <T, U, V, R> R execute(SerializableTriFunction<T, U, V, R> function, U u, V v) throws Throwable {
        T t = resolveFunction(function);
        return function.apply(t, u, v);
    }

    public <T, U, V> void execute(SerializableTriConsumer<T, U, V> function, U u, V v) throws Throwable {
        T t = resolveFunction(function);
        function.accept(t, u, v);
    }

    public <T, U, V, S, R> R execute(SerializableQuaFunction<T, U, V, S, R> function, U u, V v, S s) throws Throwable {
        T t = resolveFunction(function);
        return function.apply(t, u, v, s);
    }

    public <T, U, V, S> void execute(SerializableQuaConsumer<T, U, V, S> function, U u, V v, S s) throws Throwable {
        T t = resolveFunction(function);
        function.accept(t, u, v, s);
    }

    private <T> T resolveFunction(SerializableFunctionalInterface<T> function) {
        SerializedLambda lambda = LambdaUtil.INSTANCE.serializedLambda(function);
        int capturedArgCount = lambda.getCapturedArgCount();
        if (capturedArgCount > 0) {
            throw new IllegalArgumentException("The function must be a class method.");
        }
        String className = lambda.getImplClass().replace('/', '.');
        Class<T> clazz = ClassUtil.INSTANCE.forName(className);
        return BeanContext.INSTANCE.getBean(clazz);
    }
}
