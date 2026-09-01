package wang.liangchen.matrix.framework.ddd.domain;

import java.util.Set;

/**
 * Enumeration representing the core meta-model elements in Domain-Driven Design (DDD).
 * <p>
 * These elements define the main building blocks for modeling a domain:
 * <ul>
 *   <li><b>Entity</b>: An object defined primarily by its identity.</li>
 *   <li><b>AggregateRoot</b>: The root entity that controls access to the aggregate.</li>
 *   <li><b>ValueObject</b>: An object defined by its attributes, with no distinct identity.</li>
 *   <li><b>DomainService</b>: A stateless service encapsulating domain logic not naturally fitting in entities or value objects.</li>
 *   <li><b>DomainEvent</b>: An event representing something that happened in the domain.</li>
 *   <li><b>DomainFactory</b>: Responsible for creating complex domain objects or aggregates.</li>
 *   <li><b>Identity</b>: The Identity type.</li>
 * </ul>
 *
 * @author Liangchen.Wang
 * @since 2023-10-08
 */
public enum DomainMetaModel {
    Entity,
    AggregateRoot,
    ValueObject,
    DomainService,
    DomainEvent,
    DomainFactory,
    Identity;

    /**
     * 值对象字段允许的不可变类型全限定名集合：基本类型、包装类型、String、时间类型、
     * BigDecimal/BigInteger、UUID。集合及Optional的泛型内容无法从字段原始类型可靠验证，
     * 应封装为自身满足不可变约束的值对象。
     * 供ArchUnit规则与注解处理器共享，消除重复定义。
     */
    public static final Set<String> IMMUTABLE_TYPE_NAMES = Set.of(
            "boolean", "byte", "char", "short", "int", "long", "float", "double",
            "java.lang.Boolean", "java.lang.Byte", "java.lang.Character",
            "java.lang.Short", "java.lang.Integer", "java.lang.Long",
            "java.lang.Float", "java.lang.Double",
            "java.lang.String",
            "java.time.Instant", "java.time.LocalDate", "java.time.LocalTime",
            "java.time.LocalDateTime", "java.time.ZonedDateTime",
            "java.time.OffsetDateTime", "java.time.Year", "java.time.YearMonth",
            "java.time.MonthDay", "java.time.Duration", "java.time.Period",
            "java.math.BigDecimal", "java.math.BigInteger",
            "java.util.UUID"
    );
}