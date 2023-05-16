package wang.liangchen.matrix.framework.data.dao.entity;

import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Liangchen.Wang 2023-04-30 22:40
 */
public class Entities<S extends RootEntity> extends ArrayList<S> {
    public Entities(int initialCapacity) {
        super(initialCapacity);
    }

    public Entities() {
    }

    public Entities(Collection<? extends S> collection) {
        super(collection);
    }

    public List<S> to() {
        return new ArrayList<>(this);
    }

    public <T> List<T> to(Class<T> targetClass) {
        return ObjectUtil.INSTANCE.copyProperties(this, targetClass);
    }

    public <T> List<T> to(Class<T> targetClass, BiConsumer<S, T> biConsumer) {
        return ObjectUtil.INSTANCE.copyProperties(this, targetClass, biConsumer);
    }
}
