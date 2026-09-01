package wang.liangchen.matrix.framework.ddd.processor;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PropertyProcessorFactory {
    private static final ConcurrentMap<Class<?>, List<IPropertyProcessor>> CACHE = new ConcurrentHashMap<>();

    public static List<IPropertyProcessor> getProcessors(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, PropertyProcessorFactory::buildProcessors);
    }

    private static List<IPropertyProcessor> buildProcessors(Class<?> clazz) {
        List<IPropertyProcessor> processors = new ArrayList<>();
        if (clazz.isRecord()) {
            RecordComponent[] components = clazz.getRecordComponents();
            if (components != null) {
                for (RecordComponent component : components) {
                    processors.add(new RecordPropertyProcessor(component, component.getAccessor()));
                }
            }
            return processors;
        }
        try {
            PropertyDescriptor[] descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                // 过滤掉 Object 基类自带的 "class" 属性,只添加有 Getter 的属性（内省默认只返回有读方法的属性）
                if (!"class".equals(descriptor.getName())) {
                    processors.add(new BeanPropertyProcessor(descriptor));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to introspect class: " + clazz, e);
        }
        return processors;
    }

    public static IPropertyProcessor getProcessor(Class<?> clazz, String propertyName) {
        return getProcessors(clazz).stream()
                .filter(r -> r.getName().equals(propertyName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + propertyName + " in " + clazz));
    }
}
