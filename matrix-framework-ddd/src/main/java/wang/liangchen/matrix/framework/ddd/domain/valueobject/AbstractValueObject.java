package wang.liangchen.matrix.framework.ddd.domain.valueobject;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 值对象基类：基于属性值组合实现相等性。
 * 子类应保持不可变（类为final，属性为final），任何操作都返回新的值对象实例。
 * 相等性按子类的全部非static、非transient字段比较（含继承层次），字段为数组时按内容比较。
 *
 * @author Liangchen.Wang
 */
@DomainModel(DomainMetaModel.ValueObject)
public abstract class AbstractValueObject implements IValueObject {

    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        for (Field field : fieldsOf(getClass())) {
            if (!Objects.deepEquals(readField(field, this), readField(field, other))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (Field field : fieldsOf(getClass())) {
            Object value = readField(field, this);
            int fieldHash = Arrays.deepHashCode(new Object[]{value});
            result = 31 * result + 31 + fieldHash;
        }
        return result;
    }

    private static List<Field> fieldsOf(Class<?> type) {
        return FIELD_CACHE.computeIfAbsent(type, AbstractValueObject::resolveFields);
    }

    private static List<Field> resolveFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null && !current.equals(AbstractValueObject.class); current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }
                field.setAccessible(true);
                fields.add(field);
            }
        }
        return List.copyOf(fields);
    }

    private static Object readField(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read field " + field.getName() + " of " + target.getClass().getName(), e);
        }
    }
}
