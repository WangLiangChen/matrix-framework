package wang.liangchen.matrix.framework.commons.object;

import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.exception.MessageLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * @author Liangchen.Wang 2022-04-01 21:46
 */
public class EnhancedList<E> implements List<E>, RandomAccess, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final List<E> delegate;

    public EnhancedList() {
        this.delegate = new ArrayList<>();
    }

    public EnhancedList(int initialCapacity) {
        this.delegate = new ArrayList<>(initialCapacity);
    }

    public EnhancedList(List<E> delegate) {
        ValidationUtil.INSTANCE.notNull(MessageLevel.WARN, delegate, "parameter must not be null");
        this.delegate = delegate;
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        delegate.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        delegate.sort(c);
    }

    @Override
    public Spliterator<E> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return delegate.toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return delegate.removeIf(filter);
    }

    @Override
    public Stream<E> stream() {
        return delegate.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return delegate.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        delegate.forEach(action);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return delegate.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.contains(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public E get(int index) {
        return delegate.get(index);
    }

    @Override
    public E set(int index, E element) {
        return delegate.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        delegate.add(index, element);
    }

    @Override
    public E remove(int index) {
        return delegate.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    public EnhancedList<E> fluentAdd(E e) {
        this.add(e);
        return this;
    }


    public EnhancedList<E> fluentRemove(E e) {
        this.remove(e);
        return this;
    }


    public EnhancedList<E> fluentAddAll(Collection<? extends E> collection) {
        this.addAll(collection);
        return this;
    }


    public EnhancedList<E> fluentAddAll(int index, Collection<? extends E> collection) {
        this.addAll(index, collection);
        return this;
    }


    public EnhancedList<E> fluentRemoveAll(Collection<E> collection) {
        this.removeAll(collection);
        return this;
    }


    public EnhancedList<E> fluentRetainAll(Collection<E> collection) {
        this.retainAll(collection);
        return this;
    }


    public EnhancedList<E> fluentClear() {
        this.clear();
        return this;
    }


    public EnhancedList<E> fluentSet(int index, E element) {
        this.set(index, element);
        return this;
    }


    public EnhancedList<E> fluentAdd(int index, E element) {
        this.add(index, element);
        return this;
    }


    public EnhancedList<E> fluentRemove(int index) {
        this.remove(index);
        return this;
    }

    public List<E> getNativeList() {
        return delegate;
    }

    @SuppressWarnings("unchecked")
    public <K, V> EnhancedMap<K, V> getEnhancedMap(int index) {
        Object object = this.get(index);
        if (null == object) {
            return null;
        }
        if (object instanceof EnhancedMap) {
            return (EnhancedMap<K, V>) object;
        }
        if (object instanceof Map) {
            return new EnhancedMap<>((Map<K, V>) object);
        }
        throw new MatrixWarnException("object must be Map or EnhancedMap");
    }

    @SuppressWarnings("unchecked")
    public EnhancedList<E> getEnhancedList(int index) {
        Object object = this.get(index);
        if (null == object) {
            return null;
        }
        if (object instanceof EnhancedList) {
            return (EnhancedList<E>) object;
        }

        if (object instanceof List) {
            return new EnhancedList<>((List<E>) object);
        }

        throw new MatrixWarnException("object must be List or EnhancedList");
    }

    public <T> T getObject(int index) {
        Object object = get(index);
        return ObjectUtil.INSTANCE.cast(object);
    }

    public byte[] getBytes(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBytes(object);
    }

    public Boolean getBoolean(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBoolean(object);
    }

    public boolean getBooleanValue(int index) {
        Boolean object = getBoolean(index);
        if (null == object) {
            return false;
        }
        return object;
    }


    public Byte getByte(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToByte(object);
    }

    public byte getByteValue(int index) {
        Byte object = getByte(index);
        if (null == object) {
            return 0;
        }
        return object;
    }

    public Short getShort(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToShort(object);
    }

    public short getShortValue(int index) {
        Short object = getShort(index);
        if (object == null) {
            return 0;
        }
        return object;
    }

    public Integer getInteger(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToInt(object);
    }

    public int getIntegerValue(int index) {
        Integer object = getInteger(index);
        if (null == object) {
            return 0;
        }
        return object;
    }

    public Long getLong(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToLong(object);
    }

    public long getLongValue(int index) {
        Long object = getLong(index);
        if (object == null) {
            return 0L;
        }
        return object;
    }

    public Float getFloat(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToFloat(object);
    }

    public float getFloatValue(int index) {
        Float object = getFloat(index);
        if (object == null) {
            return 0f;
        }
        return object;
    }

    public Double getDouble(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToDouble(object);
    }

    public double getDoubleValue(int index) {
        Double object = getDouble(index);
        if (object == null) {
            return 0d;
        }
        return object;
    }

    public BigDecimal getBigDecimal(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBigDecimal(object);
    }

    public BigInteger getBigInteger(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBigInteger(object);
    }

    public String getString(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }

}
