package wang.liangchen.matrix.framework.ddd.specification;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpecificationTest {

    private final User alice = new User("Alice", "admin", true,
            LocalDateTime.of(1990, 1, 1, 0, 0), 30, new BigDecimal("10000"));
    private final User tom = new User("TOM", "manager", true,
            LocalDateTime.of(1985, 1, 2, 3, 4), 20, new BigDecimal("5000"));
    private final User bob = new User("BOB", "employee", false,
            LocalDateTime.of(2000, 1, 3, 4, 5), 10, new BigDecimal("3000"));

    @Test
    void shouldEvaluateInMemoryPredicateAndConvertToNestedSql() {
        Specification<User> specification = Specification.where(
                Specification.<User, Integer>field(User::getAge, "age").between(18, 65)
        ).and(
                Specification.<User, String>field(User::getRole, "role").equalToIgnoreCase("ADMIN")
                        .or(Specification.<User, BigDecimal>field(User::getSalary, "salary").greaterThan(new BigDecimal("6000")))
        ).and(
                Specification.<User, Boolean>field(User::isActive, "active").equalTo(true)
        );

        assertTrue(specification.isSatisfiedBy(alice));
        assertFalse(specification.isSatisfiedBy(tom));
        assertFalse(specification.isSatisfiedBy(bob));
        System.out.println(specification.toSql());
        assertEquals("((age BETWEEN 18 AND 65 AND (UPPER(role) = UPPER('ADMIN') OR salary > 6000)) AND active = 1)",
                specification.toSql());
    }

    @Test
    void shouldParseNestedSqlBackToSpecification() {
        Specification<User> specification = Specification.fromSql(
                "(age BETWEEN 18 AND 65 AND (UPPER(role) = UPPER('ADMIN') OR salary > 6000)) AND active = 1",
                SpecificationField.of("age", Integer.class, User::getAge),
                SpecificationField.of("role", String.class, User::getRole),
                SpecificationField.of("salary", BigDecimal.class, User::getSalary),
                SpecificationField.of("active", Boolean.class, User::isActive)
        );

        assertTrue(specification.isSatisfiedBy(alice));
        assertFalse(specification.isSatisfiedBy(tom));
        assertFalse(specification.isSatisfiedBy(bob));
        assertEquals("((age BETWEEN 18 AND 65 AND (UPPER(role) = UPPER('ADMIN') OR salary > 6000)) AND active = 1)",
                specification.toSql());
    }

    static final class User {
        private final String name;
        private final String role;
        private final boolean active;
        private final LocalDateTime birthday;
        private final Integer age;
        private final BigDecimal salary;

        User(String name, String role, boolean active, LocalDateTime birthday, Integer age, BigDecimal salary) {
            this.name = name;
            this.role = role;
            this.active = active;
            this.birthday = birthday;
            this.age = age;
            this.salary = salary;
        }

        String getName() {
            return name;
        }

        String getRole() {
            return role;
        }

        boolean isActive() {
            return active;
        }

        LocalDateTime getBirthday() {
            return birthday;
        }

        Integer getAge() {
            return age;
        }

        BigDecimal getSalary() {
            return salary;
        }
    }
}
