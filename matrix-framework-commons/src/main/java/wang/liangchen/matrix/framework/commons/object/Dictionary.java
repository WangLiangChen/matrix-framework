package wang.liangchen.matrix.framework.commons.object;


import java.io.Serializable;
import java.util.*;

/**
 * @author Liangchen.Wang 2022-12-12 13:05
 */
public final class Dictionary implements Serializable, Map<String, Object> {
    private final Map<String, Object> delegate;

    public Dictionary() {
        this(new EnhancedMap<>());
    }

    public Dictionary(Map<String, Object> map) {
        this.delegate = map;
    }

    public static Dictionary newInstance(Map<String, Object> map) {
        return new Dictionary(map);
    }

    public static Dictionary newInstance() {
        return new Dictionary();
    }

    public Map<String, Object> nativeMap() {
        return this.delegate;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public Object put(String key, Object value) {
        return null;
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return Set.of();
    }

    @Override
    public Collection<Object> values() {
        return List.of();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return Set.of();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(delegate);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Dictionary dictionary = (Dictionary) object;
        return Objects.equals(this.delegate, dictionary.delegate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Dictionary[", "]")
                .add("delegate=" + delegate)
                .toString();
    }
}
