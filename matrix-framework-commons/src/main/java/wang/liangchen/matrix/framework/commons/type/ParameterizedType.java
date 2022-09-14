package wang.liangchen.matrix.framework.commons.type;

import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

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
public class ParameterizedType implements java.lang.reflect.ParameterizedType {
    private final Class raw;
    private final Type[] args;
    private final Type owner;

    public ParameterizedType(Class raw, Type[] args) {
        this(raw, args, null);

    }

    public ParameterizedType(Class raw, Type[] args, Type owner) {
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
        this.owner = owner;
        checkArgs();
    }

    private void checkArgs() {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN,raw, "raw class must not be null");
        TypeVariable[] typeParameters = raw.getTypeParameters();
        if (args.length != 0 && typeParameters.length != args.length) {
            throw new MatrixWarnException(raw.getName() + " expect " + typeParameters.length + " arg(s), got " + args.length);
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return owner;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(raw.getName());
        if (args.length != 0) {
            sb.append('<');
            for (int i = 0; i < args.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                Type type = args[i];
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
                    sb.append(args[i].toString());
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

        ParameterizedType that = (ParameterizedType) o;

        if (!raw.equals(that.raw)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(args, that.args)) {
            return false;
        }
        return Objects.equals(owner, that.owner);

    }

    @Override
    public int hashCode() {
        int result = raw.hashCode();
        result = 31 * result + Arrays.hashCode(args);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }
}
