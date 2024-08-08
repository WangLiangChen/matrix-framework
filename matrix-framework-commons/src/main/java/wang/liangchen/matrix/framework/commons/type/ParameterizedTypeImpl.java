package wang.liangchen.matrix.framework.commons.type;

import wang.liangchen.matrix.framework.commons.exception.MatrixExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author LiangChen.Wang
 * {@code
 * 如对象O里含有List<T>使用下面两步
 * Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
 * Type type = new ParameterizedTypeImpl(O.class, new Type[]{listType});}
 */
public class ParameterizedTypeImpl implements ParameterizedType {
    private final Class<?> rawType;
    private final Type[] actualTypeArguments;
    private final Type ownerType;

    public ParameterizedTypeImpl(Class raw, Type[] args) {
        this(raw, args, null);

    }

    public ParameterizedTypeImpl(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) {
        this.rawType = rawType;
        this.actualTypeArguments = actualTypeArguments == null ? new Type[0] : actualTypeArguments;
        this.ownerType = ownerType;
        checkArgs();
    }

    private void checkArgs() {
        ValidationUtil.INSTANCE.notNull(MatrixExceptionLevel.WARN, rawType, "raw class must not be null");
        TypeVariable[] typeParameters = rawType.getTypeParameters();
        if (actualTypeArguments.length != 0 && typeParameters.length != actualTypeArguments.length) {
            throw new MatrixWarnException(rawType.getName() + " expect " + typeParameters.length + " arg(s), got " + actualTypeArguments.length);
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rawType.getName());
        if (actualTypeArguments.length != 0) {
            sb.append('<');
            for (int i = 0; i < actualTypeArguments.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                Type type = actualTypeArguments[i];
                if (type instanceof Class) {
                    Class clazz = (Class) type;

                    if (clazz.isArray()) {
                        int count = 0;
                        do {
                            count++;
                            clazz = clazz.getComponentType();
                        } while (clazz.isArray());

                        sb.append(clazz.getName());

                        for (int j = count; j > 0; j--) {
                            sb.append("[]");
                        }
                    } else {
                        sb.append(clazz.getName());
                    }
                } else {
                    sb.append(actualTypeArguments[i].toString());
                }
            }
            sb.append('>');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;

        if (!rawType.equals(that.rawType)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(actualTypeArguments, that.actualTypeArguments)) {
            return false;
        }
        return Objects.equals(ownerType, that.ownerType);

    }

    @Override
    public int hashCode() {
        int result = rawType.hashCode();
        result = 31 * result + Arrays.hashCode(actualTypeArguments);
        result = 31 * result + (ownerType != null ? ownerType.hashCode() : 0);
        return result;
    }
}
