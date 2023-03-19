package wang.liangchen.matrix.framework.commons.builder;

import wang.liangchen.matrix.framework.commons.function.QuaConsumer;
import wang.liangchen.matrix.framework.commons.function.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Liangchen.Wang 2021-08-23 10:31
 * Generic Builder base on jdk8
 * <code>
 * GenericBuilder<Book> builder = GenericBuilder.of(Book::new);
 * builder.with(Book::setBookId,1).build();
 * </code>
 */
public final class Builder<T> {
    private final Supplier<T> constructor;
    private final List<Consumer<T>> setterInvokers = new ArrayList<>();

    private Builder(Supplier<T> constructor) {
        this.constructor = constructor;
    }

    public static <T> Builder<T> of(Supplier<T> constructor) {
        return new Builder<>(constructor);
    }

    public <V> Builder<T> with(BiConsumer<T, V> setter, V value) {
        setterInvokers.add(instance -> setter.accept(instance, value));
        return this;
    }

    public <K, V> Builder<T> with(TriConsumer<T, K, V> setter, K key, V value) {
        setterInvokers.add(instance -> setter.accept(instance, key, value));
        return this;
    }

    public <K, V, S> Builder<T> with(QuaConsumer<T, K, V, S> setter, K key, V value, S other) {
        setterInvokers.add(instance -> setter.accept(instance, key, value, other));
        return this;
    }

    public T build() {
        // 调用Supplier,使用构造器创建对象
        T instance = constructor.get();
        // 调用setter,完成赋值
        setterInvokers.forEach(setterInvoker -> setterInvoker.accept(instance));
        setterInvokers.clear();
        return instance;
    }
}
