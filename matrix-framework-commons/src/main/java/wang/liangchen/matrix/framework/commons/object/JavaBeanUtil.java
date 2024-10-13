package wang.liangchen.matrix.framework.commons.object;

import com.esotericsoftware.reflectasm.MethodAccess;
import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author Liangchen.Wang 2022-04-15 11:36
 */
public enum JavaBeanUtil {
    /**
     * instance
     */
    INSTANCE;
    private final static String SET = "set";
    private final static String GET = "get";
    private final static String IS = "is";

    public String resolveFieldName(String methodName) {
        if (methodName.startsWith(GET) || methodName.startsWith(SET)) {
            return StringUtil.INSTANCE.firstLetterLowerCase(methodName.substring(3));
        }
        if (methodName.startsWith(IS)) {
            return StringUtil.INSTANCE.firstLetterLowerCase(methodName.substring(2));
        }
        throw new MatrixWarnException("methodName is illegal");
    }

    public String resolveGetterName(String fieldName) {
        return String.format("%s%s", GET, StringUtil.INSTANCE.firstLetterUpperCase(fieldName));
    }

    public String resolveSetterName(String fieldName) {
        return String.format("%s%s", SET, StringUtil.INSTANCE.firstLetterUpperCase(fieldName));
    }

    public String resolveIsName(String fieldName) {
        return String.format("%s%s", IS, StringUtil.INSTANCE.firstLetterUpperCase(fieldName));
    }
    public <S, T> List<T> copyProperties(Collection<S> sources, Class<T> targetClass) {
        return copyProperties(sources, targetClass, null);
    }

    public <S, T> List<T> copyProperties(Collection<S> sources, Class<T> targetClass, BiConsumer<S, T> consumer) {
        if (CollectionUtil.INSTANCE.isEmpty(sources)) {
            return new ArrayList<>(0);
        }
        List<T> targets = new ArrayList<>(sources.size());
        for (S source : sources) {
            T target = copyProperties(source, targetClass);
            if (null != consumer) {
                consumer.accept(source, target);
            }
            targets.add(target);
        }
        return targets;
    }

    public <T> T copyProperties(Object source, Class<T> targetClass) {
        T target = ClassUtil.INSTANCE.instantiate(targetClass);
        copyProperties(source, target);
        return target;
    }

    public void copyProperties(Object source, Object target) {
        ValidationUtil.INSTANCE.notNull(source, "source must not be null");
        ValidationUtil.INSTANCE.notNull(target, "target must not be null");
        ClassUtil.MethodAccessor sourceMethodAccessor = ClassUtil.INSTANCE.methodAccessor(source.getClass());
        ClassUtil.MethodAccessor targetMethodAccessor = ClassUtil.INSTANCE.methodAccessor(target.getClass());
        MethodAccess sourceMethodAccess = sourceMethodAccessor.getMethodAccess();
        MethodAccess targetMethodAccess = targetMethodAccessor.getMethodAccess();
        Map<String, Integer> getters = sourceMethodAccessor.getGetters();
        Map<String, Integer> setters = targetMethodAccessor.getSetters();
        setters.forEach((setter, setterIndex) -> {
            String fieldName = setter.substring(3);
            String getter = StringUtil.INSTANCE.getGetter(fieldName);
            Integer getterIndex = getters.get(getter);
            if (null == getterIndex) {
                return;
            }
            Object returnValue = sourceMethodAccess.invoke(source, getterIndex);
            targetMethodAccess.invoke(target, setterIndex, returnValue);
        });
    }
}
