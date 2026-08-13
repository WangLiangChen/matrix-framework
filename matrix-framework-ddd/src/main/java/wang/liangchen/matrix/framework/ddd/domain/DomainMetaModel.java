package wang.liangchen.matrix.framework.ddd.domain;

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
    DomainFactory
}