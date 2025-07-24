package wang.liangchen.matrix.framework.spring.data.repository;

import wang.liangchen.matrix.framework.commons.object.EnhancedList;
import wang.liangchen.matrix.framework.commons.object.JavaBeanUtil;
import wang.liangchen.matrix.framework.spring.data.entity.RootEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Liangchen.Wang 2023-04-30 22:40
 */
public class Entities<S extends RootEntity> extends EnhancedList<S> {
    public Entities(int initialCapacity) {
        super(initialCapacity);
    }

    public Entities() {
    }

    public Entities(List<S> list) {
        super(list);
    }

    public List<S> to() {
        return new ArrayList<>(this);
    }

    public <T> List<T> to(Class<T> targetClass) {
        return JavaBeanUtil.INSTANCE.copyProperties(this, targetClass);
    }

    public <T> List<T> to(Class<T> targetClass, BiConsumer<S, T> biConsumer) {
        return JavaBeanUtil.INSTANCE.copyProperties(this, targetClass, biConsumer);
    }
}
