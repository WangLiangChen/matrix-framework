package wang.liangchen.matrix.framework.ddd.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 属性处理器接口，统一抽象 Bean 属性与 Record 组件的元数据访问与读写操作。
 *
 * <p>每个 IPropertyProcessor 实例对应类的一个属性，提供属性名称、类型、泛型签名、
 * 注解、声明类等元数据，以及运行时读写值的能力。
 *
 * <p>框架提供两种实现：
 * <ul>
 *   <li>{@link BeanPropertyProcessor} — 基于 JavaBeans Introspector，适用于常规 Bean</li>
 *   <li>{@link RecordPropertyProcessor} — 基于 {@link java.lang.reflect.RecordComponent}，适用于 Record 类</li>
 * </ul>
 *
 * @author Liangchen.Wang 2022-11-27 22:12
 * @see PropertyProcessorRegistry
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
     * 返回声明该属性的类。
     *
     * <p>对于继承场景，返回实际声明该字段的类，而非子类。
     *
     * @return 声明该属性的类
     */
    Class<?> getDeclaringClass();

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
     * 从目标对象读取该属性的值。
     *
     * @param target 目标对象实例
     * @return 属性值
     * @throws IllegalStateException 如果不可读
     */
    Object getValue(Object target);

    /**
     * 向目标对象写入该属性的值。
     *
     * @param target 目标对象实例
     * @param value  要写入的值
     * @throws IllegalStateException         如果不可写
     * @throws UnsupportedOperationException 如果不可写
     */
    void setValue(Object target, Object value);

}