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
public class EnhancedList implements List<Object>, Cloneable, RandomAccess, Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Object> list;
    protected transient Object relatedArray;
    protected transient Type componentType;

    public EnhancedList() {
        this.list = new ArrayList<>();
    }

    public EnhancedList(List<Object> list) {
        if (list == null) {
            throw new IllegalArgumentException("list is null.");
        }
        this.list = list;
    }

    public EnhancedList(int initialCapacity) {
        this.list = new ArrayList<>(initialCapacity);
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
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return list.toArray(array);
    }

    @Override
    public boolean add(Object e) {
        return list.add(e);
    }

    public EnhancedList fluentAdd(Object e) {
        list.add(e);
        return this;
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    public EnhancedList fluentRemove(Object o) {
        list.remove(o);
        return this;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        return list.addAll(c);
    }

    public EnhancedList fluentAddAll(Collection<?> c) {
        list.addAll(c);
        return this;
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        return list.addAll(index, c);
    }

    public EnhancedList fluentAddAll(int index, Collection<?> c) {
        list.addAll(index, c);
        return this;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    public EnhancedList fluentRemoveAll(Collection<?> c) {
        list.removeAll(c);
        return this;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    public EnhancedList fluentRetainAll(Collection<?> c) {
        list.retainAll(c);
        return this;
    }

    @Override
    public void clear() {
        list.clear();
    }

    public EnhancedList fluentClear() {
        list.clear();
        return this;
    }

    @Override
    public Object set(int index, Object element) {
        if (index == -1) {
            list.add(element);
            return null;
        }

        if (list.size() <= index) {
            for (int i = list.size(); i < index; ++i) {
                list.add(null);
            }
            list.add(element);
            return null;
        }

        return list.set(index, element);
    }

    public EnhancedList fluentSet(int index, Object element) {
        set(index, element);
        return this;
    }

    @Override
    public void add(int index, Object element) {
        list.add(index, element);
    }

    public EnhancedList fluentAdd(int index, Object element) {
        list.add(index, element);
        return this;
    }

    @Override
    public Object remove(int index) {
        return list.remove(index);
    }

    public EnhancedList fluentRemove(int index) {
        list.remove(index);
        return this;
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Object get(int index) {
        return list.get(index);
    }

    @SuppressWarnings("unchecked")
    public EnhancedMap getEnhancedMap(int index) {
        Object object = list.get(index);
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
        Object object = list.get(index);

        if (object instanceof EnhancedList) {
            return (EnhancedList) object;
        }

        if (object instanceof List) {
            return new EnhancedList((List) object);
        }

        throw new MatrixWarnException("object must be List or EnhancedList");
    }

    public <T> T getObject(int index) {
        Object object = list.get(index);
        return ObjectUtil.INSTANCE.cast(object);
    }

    public Boolean getBoolean(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBoolean(object);
    }

    public boolean getBooleanValue(int index) {
        Object object = get(index);
        if (object == null) {
            return false;
        }
        return ObjectUtil.INSTANCE.castToBoolean(object).booleanValue();
    }

    public Byte getByte(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToByte(object);
    }

    public byte getByteValue(int index) {
        Object object = get(index);
        if (object == null) {
            return 0;
        }
        return ObjectUtil.INSTANCE.castToByte(object).byteValue();
    }

    public Short getShort(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToShort(object);
    }

    public short getShortValue(int index) {
        Object object = get(index);
        if (object == null) {
            return 0;
        }
        return ObjectUtil.INSTANCE.castToShort(object).shortValue();
    }

    public Integer getInt(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToInt(object);
    }

    public int getIntValue(int index) {
        Object object = get(index);
        if (object == null) {
            return 0;
        }
        return ObjectUtil.INSTANCE.castToInt(object);
    }

    public Long getLong(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToLong(object);
    }

    public long getLongValue(int index) {
        Object object = get(index);
        if (object == null) {
            return 0L;
        }
        return ObjectUtil.INSTANCE.castToLong(object).longValue();
    }

    public Float getFloat(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToFloat(object);
    }

    public float getFloatValue(int index) {
        Object object = get(index);
        if (object == null) {
            return 0f;
        }
        return ObjectUtil.INSTANCE.castToFloat(object).floatValue();
    }

    public Double getDouble(int index) {
        Object object = get(index);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToDouble(object);
    }

    public double getDoubleValue(int index) {
        Object object = get(index);
        if (object == null) {
            return 0d;
        }
        return ObjectUtil.INSTANCE.castToDouble(object).doubleValue();
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
        return ObjectUtil.INSTANCE.castToString(object);
    }

    @Override
    public Object clone() {
        return new EnhancedList(new ArrayList<Object>(list));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof EnhancedList) {
            return this.list.equals(((EnhancedList) obj).list);
        }

        return this.list.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }
}
