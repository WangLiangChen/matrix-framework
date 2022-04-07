package wang.liangchen.matrix.framework.commons.builder;

import wang.liangchen.matrix.framework.commons.function.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Liangchen.Wang 2021-08-23 10:31
 * Generic Builder base on jdk8
 */
public final class GenericBuilder<T> {
    private final Supplier<T> instance;
    private final List<Consumer<T>> modifiers = new ArrayList<>();

    private GenericBuilder(Supplier<T> instance) {
        this.instance = instance;
    }

    public static <T> GenericBuilder<T> of(Supplier<T> instance) {
        return new GenericBuilder<>(instance);
    }

    public <U> GenericBuilder<T> with(BiConsumer<T, U> consumer, U value) {
        Consumer<T> c = instance -> consumer.accept(instance, value);
        modifiers.add(c);
        return this;
    }

    public <K, V> GenericBuilder<T> with(TriConsumer<T, K, V> consumer, K key, V value) {
        Consumer<T> c = instance -> consumer.accept(instance, key, value);
        modifiers.add(c);
        return this;
    }

    public T build() {
        T value = instance.get();
        modifiers.forEach(modifier -> modifier.accept(value));
        modifiers.clear();
        return value;
    }
}
