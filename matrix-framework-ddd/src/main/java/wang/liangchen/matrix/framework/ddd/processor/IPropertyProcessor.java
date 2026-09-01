package wang.liangchen.matrix.framework.ddd.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 属性处理器接口，统一抽象 Bean 属性与 Record 组件的元数据访问与读写操作。
 *
 * <p>每个 IPropertyProcessor 实例对应一个属性（字段），提供属性名称、类型、泛型签名、
 * 注解、修饰符、声明类等元数据，以及运行时读写值的能力。
 *
 * <p>框架提供两种实现：
 * <ul>
 *   <li>{@link BeanPropertyProcessor} — 基于 JavaBeans Introspector，适用于常规 Bean</li>
 *   <li>{@link RecordPropertyProcessor} — 基于 {@link java.lang.reflect.RecordComponent}，适用于 Record 类</li>
 * </ul>
 *
 * @author Liangchen.Wang 2022-11-27 22:12
 * @see PropertyProcessorFactory
 * @see BeanPropertyProcessor
 * @see RecordPropertyProcessor
 */
public interface IPropertyProcessor {

    /**
     * 返回属性名称。
     *
     * @return 属性名称，如 "orderId"
     */
    String getName();

    /**
     * 返回属性的类型（擦除后的原始类型）。
     *
     * @return 属性类型，如 {@code List.class}
     * @see #getGenericType() 可获取保留泛型信息的类型
     */
    Class<?> getType();

    /**
     * 返回属性的泛型类型签名，保留完整的泛型参数信息。
     *
     * <p>例如属性声明为 {@code List<String>} 时，{@link #getType()} 返回 {@code List.class}，
     * 而本方法返回包含 {@code String} 参数的 {@link java.lang.reflect.ParameterizedType}。
     *
     * @return 属性的泛型类型；若无法获取泛型信息，回退为 {@link #getType()} 的结果
     */
    Type getGenericType();

    /**
     * 从目标对象读取该属性的值。
     *
     * @param target 目标对象实例
     * @return 属性值
     * @throws IllegalStateException 如果属性不可读（{@link #isReadable()} 返回 false）
     */
    Object getValue(Object target);

    /**
     * 向目标对象写入该属性的值。
     *
     * @param target 目标对象实例
     * @param value  要写入的值
     * @throws IllegalStateException       如果属性不可写（{@link #isWritable()} 返回 false）
     * @throws UnsupportedOperationException 如果属性类型不支持写入（如 Record 组件）
     */
    void setValue(Object target, Object value);

    /**
     * 返回该属性（字段）上直接标注的注解集合。
     *
     * <p>注意：此处返回的是属性/字段级别的注解，而非属性类型上的注解。
     * 例如 {@code @Identity private IStringIdentity orderId} 返回的是 {@code @Identity}，
     * 而非 {@code IStringIdentity} 类上的注解。
     *
     * @return 属性上的注解集合；若无注解则返回空集合
     */
    Set<Annotation> getAnnotations();

    /**
     * 返回声明该属性的类。
     *
     * <p>对于继承场景，返回实际声明该字段的类，而非子类。
     *
     * @return 声明该属性的类
     */
    Class<?> getDeclaringClass();

    /**
     * 返回该属性的修饰符位掩码，可使用 {@link java.lang.reflect.Modifier} 解码。
     *
     * <p>常见修饰符包括：{@code Modifier.PUBLIC}、{@code Modifier.PRIVATE}、
     * {@code Modifier.FINAL}、{@code Modifier.TRANSIENT} 等。
     *
     * @return 修饰符位掩码；若无法获取则返回 0
     * @see java.lang.reflect.Modifier
     */
    int getModifiers();

    /**
     * 判断该属性是否可读（是否存在读方法 / accessor）。
     *
     * <p>默认实现返回 {@code true}。Record 组件始终可读，Bean 属性取决于是否存在 getter。
     *
     * @return 如果属性可读返回 {@code true}
     */
    default boolean isReadable() {
        return true;
    }

    /**
     * 判断该属性是否可写（是否存在写方法 / setter）。
     *
     * <p>默认实现返回 {@code true}。Record 组件始终不可写，Bean 属性取决于是否存在 setter。
     *
     * @return 如果属性可写返回 {@code true}
     */
    default boolean isWritable() {
        return true;
    }

    /**
     * 从该属性的注解集合中查找指定类型的注解。
     *
     * <p>便捷方法，等价于遍历 {@link #getAnnotations()} 并按类型匹配。
     *
     * @param annotationClass 要查找的注解类型
     * @param <A>             注解类型
     * @return 匹配的注解实例；若不存在则返回 {@code null}
     */
    default <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        for (Annotation annotation : getAnnotations()) {
            if (annotationClass.isInstance(annotation)) {
                return annotationClass.cast(annotation);
            }
        }
        return null;
    }

    /**
     * 返回属性类型的所有超类型（父类与接口），不包含 {@link Object}。
     *
     * <p>遍历属性类型（{@link #getType()}）的继承链，收集所有父类与实现的接口。
     * 结果包含属性类型自身。例如 {@code ArrayList<String>} 的属性会返回
     * {@code ArrayList}、{@code AbstractList}、{@code AbstractCollection}、
     * {@code List}、{@code Collection}、{@code Iterable}、{@code RandomAccess}、
     * {@code Cloneable}、{@code Serializable} 等。
     *
     * @return 超类型集合（含属性类型自身，不含 {@link Object}）；若属性类型即为 {@link Object} 则返回空集合
     */
    default Set<Class<?>> getSuperTypes() {
        Set<Class<?>> superTypes = new HashSet<>();
        Class<?> currentClass = getType();
        while (currentClass != null && currentClass != Object.class) {
            superTypes.add(currentClass);
            superTypes.addAll(Arrays.asList(currentClass.getInterfaces()));
            currentClass = currentClass.getSuperclass();
        }
        return superTypes;
    }
}