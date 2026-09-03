package wang.liangchen.matrix.framework.ddd.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-11-27 22:12
 */
final class RecordPropertyProcessor implements IPropertyProcessor {
    private final RecordComponent component;
    private final Method accessor;
    private final Set<Annotation> annotations;

    public RecordPropertyProcessor(RecordComponent component) {
        this.component = component;
        this.accessor = component.getAccessor();
        this.annotations = Set.of(component.getAnnotations());
    }

    @Override
    public String getName() {
        return component.getName();
    }

    @Override
    public Class<?> getType() {
        return component.getType();
    }

    @Override
    public Type getGenericType() {
        return component.getGenericType();
    }

    @Override
    public Object getValue(Object target) {
        try {
            return accessor.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read record component: " + getName(), e.getCause());
        }
    }

    @Override
    public void setValue(Object target, Object value) {
        throw new UnsupportedOperationException("Record components are immutable");
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public Class<?> getDeclaringClass() {
        return accessor.getDeclaringClass();
    }
}