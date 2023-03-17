package wang.liangchen.matrix.framework.commons.collection;

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
        collection.stream().forEach(consumer);
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

    public boolean isEmpty(Iterator<?> iterator) {
        if (null == iterator) {
            return true;
        }
        return !iterator.hasNext();
    }

    public boolean isNotEmpty(Iterator<?> iterator) {
        return !isEmpty(iterator);
    }

    public boolean isEmpty(Iterable<?> iterable) {
        if (null == iterable) {
            return true;
        }
        return !iterable.iterator().hasNext();
    }

    public boolean isNotEmpty(Iterable<?> iterable) {
        return !isEmpty(iterable);
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

    public <T> List<T> iterable2List(Iterable<T> iterable) {
        if (isEmpty(iterable)) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>();
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public <T> Set<T> iterator2Set(Iterable<T> iterable) {
        if (isEmpty(iterable)) {
            return Collections.emptySet();
        }
        Set<T> set = new HashSet<>();
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            set.add(iterator.next());
        }
        return set;
    }

    public <T> boolean contains(T[] array, T value) {
        if (isEmpty(array)) {
            return false;
        }
        for (T t : array) {
            if (t.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public <T> boolean notContains(T[] array, T value) {
        return !contains(array, value);
    }

}
