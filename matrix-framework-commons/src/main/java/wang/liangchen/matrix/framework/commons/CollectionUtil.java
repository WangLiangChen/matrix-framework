package wang.liangchen.matrix.framework.commons;

import java.util.*;
import java.util.function.Consumer;

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

    public <T> boolean isEmpty(Collection<T> collection) {
        if (null == collection) {
            return true;
        }
        return collection.isEmpty();
    }

    public <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public <T> boolean isNotEmpty(Collection<T> collection, Consumer<T> consumer) {
        if (isEmpty(collection)) {
            return false;
        }
        collection.forEach(consumer);
        return true;
    }

    public <T> boolean isEmpty(T[] array) {
        if (null == array) {
            return true;
        }
        return array.length == 0;
    }

    public <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public <T> boolean isNotEmpty(T[] array, Consumer<T> consumer) {
        if (isEmpty(array)) {
            return false;
        }
        Arrays.stream(array).forEach(consumer);
        return true;
    }

    public <T> boolean isEmpty(Iterator<T> iterator) {
        if (null == iterator) {
            return true;
        }
        return iterator.hasNext();
    }

    public <T> boolean isNotEmpty(Iterator<T> iterator) {
        return !isEmpty(iterator);
    }

    public <T> boolean isNotEmpty(Iterator<T> iterator, Consumer<T> consumer) {
        if (isEmpty(iterator)) {
            return false;
        }
        iterator.forEachRemaining(consumer);
        return true;
    }

    public <T> boolean isEmpty(Iterable<T> iterable) {
        if (null == iterable) {
            return true;
        }
        return iterable.iterator().hasNext();
    }

    public <T> boolean isNotEmpty(Iterable<T> iterable) {
        return !isEmpty(iterable);
    }

    public <T> boolean isNotEmpty(Iterable<T> iterable, Consumer<T> consumer) {
        if (isEmpty(iterable)) {
            return false;
        }
        iterable.forEach(consumer);
        return true;
    }


    public <K, V> boolean isEmpty(Map<K, V> map) {
        if (null == map) {
            return true;
        }
        return map.isEmpty();
    }

    public <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public <K, V> boolean isNotEmpty(Map<K, V> map, Consumer<Map.Entry<K, V>> consumer) {
        if (isEmpty(map)) {
            return false;
        }
        map.entrySet().forEach(consumer);
        return true;
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

    public <T> boolean contains(Collection<T> collection, T element) {
        if (isEmpty(collection)) {
            return false;
        }
        return collection.contains(element);
    }

    public <T> boolean notContains(Collection<T> collection, T element) {
        return !contains(collection, element);
    }

    public <T> boolean contains(T[] array, T element) {
        if (isEmpty(array)) {
            return false;
        }
        for (T t : array) {
            if (t.equals(element)) {
                return true;
            }
        }
        return false;
    }

    public <T> boolean notContains(T[] array, T value) {
        return !contains(array, value);
    }

}
