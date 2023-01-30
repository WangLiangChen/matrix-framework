package wang.liangchen.matrix.framework.commons.cache;

import java.util.LinkedHashMap;
import java.util.Map;

class LruMap<K, V> extends LinkedHashMap<K, ExpiredValue<V>> {
    private final int capacity;

    public LruMap(int capacity) {
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, ExpiredValue<V>> eldest) {
        ExpiredValue<V> expiredValue = eldest.getValue();
        if (expiredValue.isExpired()) {
            return true;
        }
        return this.size() > capacity;
    }
}
