package wang.liangchen.matrix.framework.commons.object;

import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Liangchen.Wang 2022-04-01 21:46
 */
public class EnhancedList implements List<Object>, RandomAccess, Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Object> delegate;
    protected transient Object relatedArray;
    protected transient Type componentType;

    public EnhancedList() {
        this.delegate = new ArrayList<>();
    }

    public EnhancedList(List<Object> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("list is null.");
        }
        this.delegate = delegate;
    }

    public EnhancedList(int initialCapacity) {
        this.delegate = new ArrayList<>(initialCapacity);
    }

    public Object getRelatedArray() {
        return relatedArray;
    }

    public void setRelatedArray(Object relatedArray) {
        this.relatedArray = relatedArray;
    }

    public Type getComponentType() {
        return componentType;
    }

    public void setComponentType(Type componentType) {
        this.componentType = componentType;
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
    public Iterator<Object> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return delegate.toArray(array);
    }

    @Override
    public boolean add(Object e) {
        return delegate.add(e);
    }

    public EnhancedList fluentAdd(Object e) {
        delegate.add(e);
        return this;
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    public EnhancedList fluentRemove(Object o) {
        delegate.remove(o);
        return this;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        return delegate.addAll(c);
    }

    public EnhancedList fluentAddAll(Collection<?> c) {
        delegate.addAll(c);
        return this;
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        return delegate.addAll(index, c);
    }

    public EnhancedList fluentAddAll(int index, Collection<?> c) {
        delegate.addAll(index, c);
        return this;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    public EnhancedList fluentRemoveAll(Collection<?> c) {
        delegate.removeAll(c);
        return this;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    public EnhancedList fluentRetainAll(Collection<?> c) {
        delegate.retainAll(c);
        return this;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    public EnhancedList fluentClear() {
        delegate.clear();
        return this;
    }

    @Override
    public Object set(int index, Object element) {
        if (index == -1) {
            delegate.add(element);
            return null;
        }

        if (delegate.size() <= index) {
            for (int i = delegate.size(); i < index; ++i) {
                delegate.add(null);
            }
            delegate.add(element);
            return null;
        }

        return delegate.set(index, element);
    }

    public EnhancedList fluentSet(int index, Object element) {
        set(index, element);
        return this;
    }

    @Override
    public void add(int index, Object element) {
        delegate.add(index, element);
    }

    public EnhancedList fluentAdd(int index, Object element) {
        delegate.add(index, element);
        return this;
    }

    @Override
    public Object remove(int index) {
        return delegate.remove(index);
    }

    public EnhancedList fluentRemove(int index) {
        delegate.remove(index);
        return this;
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
    public ListIterator<Object> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    @Override
    public Object get(int index) {
        return delegate.get(index);
    }

    @SuppressWarnings("unchecked")
    public EnhancedMap getEnhancedMap(int index) {
        Object object = delegate.get(index);
        if (object instanceof EnhancedMap) {
            return (EnhancedMap) object;
        }

        if (object instanceof Map) {
            return new EnhancedMap((Map) object);
        }
        throw new MatrixWarnException("object must be Map or EnhancedMap");
    }

    @SuppressWarnings("unchecked")
    public EnhancedList getEnhancedList(int index) {
        Object object = delegate.get(index);

        if (object instanceof EnhancedList) {
            return (EnhancedList) object;
        }

        if (object instanceof List) {
            return new EnhancedList((List<Object>) object);
        }

        throw new MatrixWarnException("object must be List or EnhancedList");
    }

    public <T> T getObject(int index) {
        Object object = delegate.get(index);
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
