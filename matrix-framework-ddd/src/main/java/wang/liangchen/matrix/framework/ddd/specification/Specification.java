package wang.liangchen.matrix.framework.ddd.specification;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Specification pattern implementation based on {@link Predicate}.
 * <p>
 * Supports logical AND, OR, NOT operations.
 * Evaluation order is determined by Java method-call nesting (composition tree),
 * and {@link #toSql()} emits a parenthesized SQL predicate tree for SQL-backed specifications.
 * </p>
 *
 * <h3>Basic Usage</h3>
 * <pre>{@code
 * // Define atomic specifications
 * Specification<Account> isActive = Specification.of(Account::isActive, "isActive");
 * Specification<Account> isAdmin  = Specification.of(a -> a.getRole() == Role.ADMIN, "isAdmin");
 * Specification<Account> isLocked = Specification.of(Account::isLocked, "isLocked");
 *
 * // Simple AND
 * Specification<Account> activeAdmin = isActive.and(isAdmin);
 *
 * // With brackets: isActive AND (isAdmin OR NOT isLocked)
 * // Java method nesting determines evaluation order; auto-parens handle toString()
 * Specification<Account> complex = Specification.where(isActive)
 *         .and(isAdmin.or(isLocked.not()));
 *
 * // Evaluate
 * boolean satisfied = complex.isSatisfiedBy(account);
 *
 * // Also works as Predicate in Stream
 * List<Account> result = accounts.stream().filter(complex).collect(Collectors.toList());
 * }</pre>
 *
 * <h3>Field-level SQL-like Operators</h3>
 * <pre>{@code
 * // SQL-style field comparisons via Specification.field()
 * Specification<User> spec = Specification.where(
 *         Specification.<User, String>field(User::getName, "name").like("A%")
 *     ).and(
 *         Specification.<User, Integer>field(User::getAge, "age").between(18, 65)
 *     ).and(
 *         Specification.<User, String>field(User::getRole, "role").in("ADMIN", "MODERATOR")
 *     );
 * }</pre>
 *
 * <h3>Parenthesization</h3>
 * <p>Parentheses are handled automatically:</p>
 * <ul>
 *   <li>Java method-call nesting determines evaluation order:
 *       {@code spec1.and(spec2.or(spec3))} evaluates as {@code spec1 AND (spec2 OR spec3)}</li>
 *   <li>{@code toString()} auto-parenthesizes based on SQL operator precedence:
 *       OR inside AND is wrapped, compound expressions inside NOT are wrapped</li>
 * </ul>
 *
 * @param <T> the type of the candidate object to evaluate
 * @author Liangchen.Wang
 */
public abstract class Specification<T> implements Predicate<T> {

    // ==================== Core Abstract Method ====================

    /**
     * Evaluates whether the given candidate satisfies this specification.
     *
     * @param candidate the object to evaluate
     * @return {@code true} if the candidate satisfies this specification
     */
    public abstract boolean isSatisfiedBy(T candidate);

    /**
     * Bridges to {@link Predicate#test(Object)}, delegates to {@link #isSatisfiedBy(Object)}.
     */
    @Override
    public final boolean test(T t) {
        return isSatisfiedBy(t);
    }

    /**
     * Converts this specification to a SQL predicate fragment.
     * <p>
     * Specifications built by {@link #field(Function, String)}, {@link #any()}, {@link #none()}, and logical
     * combinators are SQL-backed. Specifications built from arbitrary predicates must provide a SQL fragment via
     * {@link #of(Predicate, String)} if they need SQL conversion.
     * </p>
     *
     * @return SQL predicate fragment without the {@code WHERE} keyword
     * @throws IllegalStateException if this specification has no SQL representation
     */
    public String toSql() {
        return toString();
    }

    // ==================== Logical Combinators ====================

    /**
     * Returns a composed specification: {@code this AND other}.
     *
     * @param other the specification to compose with AND
     * @return a new AND-composed specification
     */
    @Override
    public Specification<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other, "other specification must not be null");
        return new AndSpecification<>(this, wrap(other));
    }

    /**
     * Returns a composed specification: {@code this OR other}.
     *
     * @param other the specification to compose with OR
     * @return a new OR-composed specification
     */
    @Override
    public Specification<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other, "other specification must not be null");
        return new OrSpecification<>(this, wrap(other));
    }

    /**
     * Returns the negation of this specification: {@code NOT this}.
     *
     * @return a new negated specification
     */
    public Specification<T> not() {
        return new NotSpecification<>(this);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link #not()}.</p>
     */
    @Override
    public Specification<T> negate() {
        return not();
    }

    // ==================== Static Factories ====================

    /**
     * Creates a specification from a {@link Predicate}.
     *
     * @param predicate the predicate to wrap
     * @param <T>       the candidate type
     * @return a new specification wrapping the given predicate
     */
    public static <T> Specification<T> of(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate must not be null");
        return new PredicateSpecification<>(predicate, null);
    }

    /**
     * Creates a specification from a {@link Predicate} with a human-readable description.
     *
     * @param predicate   the predicate to wrap
     * @param description a human-readable description for {@code toString()}
     * @param <T>         the candidate type
     * @return a new specification wrapping the given predicate
     */
    public static <T> Specification<T> of(Predicate<T> predicate, String description) {
        Objects.requireNonNull(predicate, "predicate must not be null");
        return new PredicateSpecification<>(predicate, description);
    }

    /**
     * Parses a SQL predicate fragment into a {@link Specification} using field extractor functions.
     *
     * @param sql            SQL predicate fragment without the {@code WHERE} keyword
     * @param fieldResolvers field name to extractor map, matched case-insensitively
     * @param <T>            candidate object type
     * @return parsed specification
     */
    public static <T> Specification<T> fromSql(String sql, Map<String, Function<T, ?>> fieldResolvers) {
        return SpecificationParser.parse(sql, fieldResolvers);
    }

    /**
     * Parses a SQL predicate fragment into a {@link Specification} using typed field metadata.
     *
     * @param sql    SQL predicate fragment without the {@code WHERE} keyword
     * @param fields typed field metadata
     * @param <T>    candidate object type
     * @return parsed specification
     */
    public static <T> Specification<T> fromSql(String sql, Collection<SpecificationField<T, ?>> fields) {
        return SpecificationParser.parse(sql, fields);
    }

    /**
     * Parses a SQL predicate fragment into a {@link Specification} using typed field metadata.
     *
     * @param sql    SQL predicate fragment without the {@code WHERE} keyword
     * @param fields typed field metadata
     * @param <T>    candidate object type
     * @return parsed specification
     */
    @SafeVarargs
    public static <T> Specification<T> fromSql(String sql, SpecificationField<T, ?>... fields) {
        return SpecificationParser.parse(sql, fields);
    }

    // ==================== Field-level SQL-like Operators ====================

    /**
     * Creates a {@link FieldSpecification} for the given field extractor.
     * <p>
     * Provides SQL-style operators: {@code =, <>, >, >=, <, <=, BETWEEN, IN, NOT IN, LIKE}, etc.
     * </p>
     *
     * <pre>{@code
     * Specification.<User, Integer>field(User::getAge).between(18, 65);
     * Specification.<User, String>field(User::getRole).in("ADMIN", "USER");
     * }</pre>
     *
     * @param extractor a function to extract the field value from the candidate
     * @param <T>       the candidate type
     * @param <V>       the field value type
     * @return a {@link FieldSpecification} for fluent operator chaining
     */
    public static <T, V> FieldSpecification<T, V> field(Function<T, V> extractor) {
        return new FieldSpecification<>(extractor, null);
    }

    /**
     * Creates a {@link FieldSpecification} with a descriptive field name for readable {@code toString()} output.
     *
     * <pre>{@code
     * Specification.<User, Integer>field(User::getAge, "age").greaterThan(18);
     * // toString() => "age > 18"
     * }</pre>
     *
     * @param extractor a function to extract the field value from the candidate
     * @param fieldName the human-readable field name used in {@code toString()}
     * @param <T>       the candidate type
     * @param <V>       the field value type
     * @return a {@link FieldSpecification} for fluent operator chaining
     */
    public static <T, V> FieldSpecification<T, V> field(Function<T, V> extractor, String fieldName) {
        return new FieldSpecification<>(extractor, fieldName);
    }

    /**
     * Entry point for building specification chains. Improves readability.
     *
     * <pre>{@code
     * Specification.where(isActive).and(isAdmin).or(isRoot);
     * }</pre>
     *
     * @param specification the initial specification
     * @param <T>           the candidate type
     * @return the given specification unchanged
     */
    public static <T> Specification<T> where(Specification<T> specification) {
        Objects.requireNonNull(specification, "specification must not be null");
        return specification;
    }

    /**
     * Static factory for negation: {@code NOT specification}.
     *
     * @param specification the specification to negate
     * @param <T>           the candidate type
     * @return a negated specification
     */
    public static <T> Specification<T> not(Specification<T> specification) {
        Objects.requireNonNull(specification, "specification must not be null");
        return new NotSpecification<>(specification);
    }

    /**
     * Returns a specification that combines all given specifications with AND.
     *
     * @param specifications the specifications to combine
     * @param <T>            the candidate type
     * @return a specification that is satisfied only when ALL given specifications are satisfied
     */
    @SafeVarargs
    public static <T> Specification<T> allOf(Specification<T>... specifications) {
        Objects.requireNonNull(specifications, "specifications must not be null");
        if (specifications.length == 0) {
            return any();
        }
        return Arrays.stream(specifications)
                .reduce(Specification::and)
                .orElseGet(Specification::any);
    }

    /**
     * Returns a specification that combines all given specifications with OR.
     *
     * @param specifications the specifications to combine
     * @param <T>            the candidate type
     * @return a specification that is satisfied when ANY of the given specifications is satisfied
     */
    @SafeVarargs
    public static <T> Specification<T> anyOf(Specification<T>... specifications) {
        Objects.requireNonNull(specifications, "specifications must not be null");
        if (specifications.length == 0) {
            return none();
        }
        return Arrays.stream(specifications)
                .reduce(Specification::or)
                .orElseGet(Specification::none);
    }

    /**
     * Returns a specification that always evaluates to {@code true}.
     *
     * @param <T> the candidate type
     * @return a tautology specification
     */
    public static <T> Specification<T> any() {
        return new PredicateSpecification<>(t -> true, "1 = 1");
    }

    /**
     * Returns a specification that always evaluates to {@code false}.
     *
     * @param <T> the candidate type
     * @return a contradiction specification
     */
    public static <T> Specification<T> none() {
        return new PredicateSpecification<>(t -> false, "1 = 0");
    }

    // ==================== Helper ====================

    @SuppressWarnings("unchecked")
    static <T> Specification<T> wrap(Predicate<? super T> predicate) {
        if (predicate instanceof Specification) {
            return (Specification<T>) predicate;
        }
        return new PredicateSpecification<>((Predicate<T>) predicate, null);
    }
}

