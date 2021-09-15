package liangchen.wang.matrix.framework.commons.utils;

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

    public <T> boolean isEmpty(T[] array) {
        return (null == array || array.length == 0);
    }

    public <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public boolean isEmpty(byte[] array) {
        return (null == array || array.length == 0);
    }

    public boolean isNotEmpty(byte[] array) {
        return !isEmpty(array);
    }

    public boolean isEmpty(short[] array) {
        return (null == array || array.length == 0);
    }

    public boolean isNotEmpty(short[] array) {
        return !isEmpty(array);
    }

    public boolean isEmpty(int[] array) {
        return (null == array || array.length == 0);
    }

    public boolean isNotEmpty(int[] array) {
        return !isEmpty(array);
    }

    public boolean isEmpty(long[] array) {
        return (null == array || array.length == 0);
    }

    public boolean isNotEmpty(long[] array) {
        return !isEmpty(array);
    }

    public boolean isEmpty(float[] array) {
        return (null == array || array.length == 0);
    }

    public boolean isNotEmpty(float[] array) {
        return !isEmpty(array);
    }

    public boolean isEmpty(double[] array) {
        return (null == array || array.length == 0);
    }

    public boolean isNotEmpty(double[] array) {
        return !isEmpty(array);
    }

    public boolean isEmpty(boolean[] array) {
        return (null == array || array.length == 0);
    }

    public boolean isNotEmpty(boolean[] array) {
        return !isEmpty(array);
    }


    public <T> boolean isEmpty(Collection<T> collection) {
        return (null == collection || collection.isEmpty());
    }

    public <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public <T> boolean isEmpty(Iterator<T> iterator) {
        return (null == iterator || !iterator.hasNext());
    }

    public <T> boolean isNotEmpty(Iterator<T> iterator) {
        return !isEmpty(iterator);
    }

    public <K, V> boolean isEmpty(Map<K, V> map) {
        return (null == map || map.isEmpty());
    }

    public <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public <T> boolean contains(T[] array, T value) {
        if (isEmpty(array)) {
            return false;
        }
        return Arrays.asList(array).contains(value);
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
}
