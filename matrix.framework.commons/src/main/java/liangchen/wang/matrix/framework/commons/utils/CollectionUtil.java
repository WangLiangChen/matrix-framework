package liangchen.wang.matrix.framework.commons.utils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Liangchen.Wang 2021-08-19 19:53
 */
public enum CollectionUtil {
    /**
     * instance
     */
    INSTANCE;

    public boolean isArray(Object object) {
        if (null == object) {
            return false;
        }
        return object.getClass().isArray();
    }

    public boolean isEmpty(Object object) {
        if (null == object) {
            return true;
        }
        if (object instanceof String) {
            return object.toString().isEmpty();
        }
        if (object instanceof Map) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        }
        Class<?> type = object.getClass();
        if (type.isArray()) {
            return Array.getLength(object) == 0;
        }
        return true;
    }

    public boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public <T> boolean contains(T[] array, T value) {
        if (isEmpty(array)) {
            return false;
        }
        return array2Set(array).contains(value);
    }

    public <T> boolean notContains(T[] array, T value) {
        return !contains(array, value);
    }


    public <T> List<T> array2List(T[] array) {
        if (isEmpty(array)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(Arrays.asList(array));
    }

    public <T> Set<T> array2Set(T[] array) {
        if (isEmpty(array)) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(array));
    }

    public <T> List<T> iterator2List(Iterator<T> iterator) {
        if (isEmpty(iterator)) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public <T> Set<T> iterator2Set(Iterator<T> iterator) {
        if (isEmpty(iterator)) {
            return Collections.emptySet();
        }
        Set<T> set = new HashSet<>();
        while (iterator.hasNext()) {
            set.add(iterator.next());
        }
        return set;
    }

}
