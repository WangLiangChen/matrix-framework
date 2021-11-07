package wang.liangchen.matrix.framework.commons.type;


import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiangChen.Wang
 * @date 18-12-18 上午10:40
 */

/**
 * Example for List<String>:
 * Type type = GenericTypeBuilder.newInstance(List.class)
 * .addTypeParam(String.class)
 * .build();
 * Example for List<? super String>:
 * Type type = GenericTypeBuilder.newInstance(List.class)
 * .addTypeParamSuper(String.class)
 * .build();
 * Example for List<? extends CharSequence>:
 * Type type = GenericTypeBuilder.newInstance(List.class)
 * .addTypeParamExtends(CharSequence.class)
 * .build();
 * Example for Map<String, String[]>:
 * Type type = GenericTypeBuilder.newInstance(HashMap.class)
 * .addTypeParam(String.class)
 * .addTypeParam(String[].class)
 * .build();
 * Example for Map<String, List<String>>:
 * Type type = GenericTypeBuilder.newInstance(Map.class)
 * .addTypeParam(String.class)
 * .beginSubType(List.class) //开始 List<String> 部分
 * .addTypeParam(String.class) //设置List的泛型值
 * .endSubType() //结束 List<String> 部分
 * .build();
 */
public class GenericTypeBuilder {
    private final GenericTypeBuilder parent;
    private final Class raw;
    private final List<Type> args = new ArrayList<>();

    public GenericTypeBuilder(Class raw, GenericTypeBuilder parent) {
        assert raw != null;
        this.parent = parent;
        this.raw = raw;
    }

    public static GenericTypeBuilder newInstance(Class raw) {
        return new GenericTypeBuilder(raw, null);
    }

    private static GenericTypeBuilder newInstance(Class raw, GenericTypeBuilder parent) {
        return new GenericTypeBuilder(raw, parent);
    }

    public GenericTypeBuilder beginSubType(Class raw) {
        return newInstance(raw, this);
    }

    public GenericTypeBuilder endSubType() {
        if (parent == null) {
            throw new MatrixInfoException("expect beginSubType() before endSubType()");
        }

        parent.addTypeParam(getType());

        return parent;
    }

    public GenericTypeBuilder addTypeParam(Class clazz) {
        return addTypeParam((Type) clazz);
    }

    public GenericTypeBuilder addTypeParamExtends(Class... classes) {
        if (classes == null) {
            throw new NullPointerException("addTypeParamExtends() expect not null Class");
        }

        WildcardTypeImpl wildcardType = new WildcardTypeImpl(null, classes);

        return addTypeParam(wildcardType);
    }

    public GenericTypeBuilder addTypeParamSuper(Class... classes) {
        if (classes == null) {
            throw new NullPointerException("addTypeParamSuper() expect not null Class");
        }

        WildcardTypeImpl wildcardType = new WildcardTypeImpl(classes, null);

        return addTypeParam(wildcardType);
    }

    public GenericTypeBuilder addTypeParam(Type type) {
        if (type == null) {
            throw new NullPointerException("addTypeParam expect not null Type");
        }

        args.add(type);

        return this;
    }

    public Type build() {
        if (parent != null) {
            throw new MatrixInfoException("expect endSubType() before build()");
        }

        return getType();
    }

    private Type getType() {
        if (args.isEmpty()) {
            return raw;
        }
        return new ParameterizedType(raw, args.toArray(new Type[args.size()]), null);
    }
}
