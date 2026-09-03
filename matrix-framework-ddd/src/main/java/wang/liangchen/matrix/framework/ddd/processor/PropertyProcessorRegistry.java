package wang.liangchen.matrix.framework.ddd.processor;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class PropertyProcessorRegistry {
    private static final ConcurrentMap<Class<?>, List<IPropertyProcessor>> CACHE = new ConcurrentHashMap<>();

    private PropertyProcessorRegistry() {
    }

    public static List<IPropertyProcessor> getProcessors(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, PropertyProcessorRegistry::buildProcessors);
    }

    private static List<IPropertyProcessor> buildProcessors(Class<?> clazz) {
        List<IPropertyProcessor> processors = new ArrayList<>();
        if (clazz.isRecord()) {
            RecordComponent[] components = clazz.getRecordComponents();
            if (components != null) {
                for (RecordComponent component : components) {
                    processors.add(new RecordPropertyProcessor(component));
                }
            }
            return processors;
        }
        try {
            Map<String, Field> fields = resolveFields(clazz);
            PropertyDescriptor[] descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                // 过滤掉 Object 基类自带的 "class" 属性,只添加有 Getter 的属性（内省默认只返回有读方法的属性）
                if (!"class".equals(descriptor.getName())) {
                    processors.add(new BeanPropertyProcessor(descriptor, fields.get(descriptor.getName())));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to introspect class: " + clazz, e);
        }
        return processors;
    }

    /**
     * 一次性收集类及其父类链上声明的全部字段（跳过 Object），按字段名索引。
     * 子类字段优先（putIfAbsent 保证遍历自派生类向上时最先出现的声明胜出），
     * 使各属性处理器无需各自重复向上遍历类层次即可拿到对应字段。
     */
    private static Map<String, Field> resolveFields(Class<?> clazz) {
        Map<String, Field> fields = new HashMap<>();
        for (Class<?> current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                fields.putIfAbsent(field.getName(), field);
            }
        }
        return fields;
    }

    public static IPropertyProcessor getProcessor(Class<?> clazz, String propertyName) {
        return getProcessors(clazz).stream()
                .filter(r -> r.getName().equals(propertyName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + propertyName + " in " + clazz));
    }
}
