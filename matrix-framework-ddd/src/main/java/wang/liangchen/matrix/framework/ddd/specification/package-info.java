/**
 * Specification support for in-memory domain object filtering.
 * <p>
 * This package provides a DDD-style {@link wang.liangchen.matrix.framework.ddd.specification.Specification}
 * abstraction, fluent field-level builders, and a small SQL-like parser for composing specifications.
 * The supported operators intentionally resemble SQL for readability, but evaluation follows Java
 * {@code Predicate} semantics against in-memory objects rather than full database SQL semantics.
 * </p>
 *
 * <h2>Semantics</h2>
 * <ul>
 *   <li>Specifications are evaluated in memory on domain objects.</li>
 *   <li>For parser use cases, prefer typed field metadata via
 *       {@link wang.liangchen.matrix.framework.ddd.specification.SpecificationField} when you need
 *       field-aware literal conversion, such as booleans, numeric coercion, or enum constants.</li>
 *   <li>Null handling is defined by each specification implementation and is not intended to fully model
 *       SQL three-valued logic.</li>
 *   <li>{@code toSql()} emits a parenthesized SQL predicate fragment that can be parsed back by
 *       {@link wang.liangchen.matrix.framework.ddd.specification.Specification#fromSql(String,
 *       java.util.Collection)} when the expression only uses supported operators and known fields.</li>
 * </ul>
 */
package wang.liangchen.matrix.framework.ddd.specification;

