package wang.liangchen.matrix.framework.commons.cache;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class LruMapCache<K, V> implements Map<K, V> {
    private final Duration ttl;
    private final LruMap<K, V> delegate;
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

    public LruMapCache(int capacity, Duration ttl) {
        this.ttl = ttl;
        this.delegate = new LruMap<>(capacity);
    }

    public LruMapCache(int capacity) {
        this(capacity, Duration.ZERO);
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            return this.delegate.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return this.delegate.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        readLock.lock();
        try {
            return this.delegate.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        readLock.lock();
        try {
            return this.delegate.containsValue(value);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        readLock.lock();
        try {
            ExpiredValue<V> expiredValue = this.delegate.get(key);
            if (null == expiredValue) {
                return null;
            }
            if (expiredValue.isExpired()) {
                return null;
            }
            return expiredValue.getValue();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        writeLock.lock();
        try {
            ExpiredValue<V> expiredValue = this.delegate.put(key, new ExpiredValue<>(value, ttl));
            return expiredValue.getValue();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        writeLock.lock();
        try {
            ExpiredValue<V> expiredValue = this.delegate.remove(key);
            return expiredValue.getValue();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new MatrixInfoException("Unsupported operation.");
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            this.delegate.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        readLock.lock();
        try {
            return this.delegate.keySet();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Collection<V> values() {
        readLock.lock();
        try {
            Collection<ExpiredValue<V>> expiredValues = this.delegate.values();
            return expiredValues.stream().map(e -> e.getValue()).collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        readLock.lock();
        try {
            Set<Entry<K, ExpiredValue<V>>> entries = this.delegate.entrySet();
            return entries.stream().map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getValue()))
                    .collect(Collectors.toSet());
        } finally {
            readLock.unlock();
        }
    }
}
