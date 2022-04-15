package wang.liangchen.matrix.framework.commons.collection;

import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

import java.util.*;

/**
 * @author Liangchen.Wang 2021-08-19 19:53
 */
public enum CollectionUtil {
    /**
     * instance
     */
    INSTANCE;

    public Object[] emptyArray() {
        return new Object[0];
    }

    public boolean isEmpty(Collection<?> collection) {
        return ObjectUtil.INSTANCE.isEmpty(collection);
    }

    public boolean isNotEmpty(Collection<?> collection) {
        return ObjectUtil.INSTANCE.isNotEmpty(collection);
    }

    public boolean isEmpty(Map<?, ?> map) {
        return ObjectUtil.INSTANCE.isEmpty(map);
    }

    public <T> boolean isEmpty(T[] array) {
        return ObjectUtil.INSTANCE.isEmpty(array);
    }

    public <T> boolean isNotEmpty(T[] array) {
        return ObjectUtil.INSTANCE.isNotEmpty(array);
    }

    public boolean isEmpty(Iterator<?> iterator) {
        return ObjectUtil.INSTANCE.isEmpty(iterator);
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

    public <T> boolean contains(T[] array, T value) {
        if (isEmpty(array)) {
            return false;
        }
        return array2Set(array).contains(value);
    }

    public <T> boolean notContains(T[] array, T value) {
        return !contains(array, value);
    }


}
