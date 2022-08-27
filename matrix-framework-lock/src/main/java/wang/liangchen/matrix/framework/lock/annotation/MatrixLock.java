package wang.liangchen.matrix.framework.lock.annotation;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang 2022-04-19 9:06
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MatrixLock {
    String lockKey();

    /**
     * @return Can be either time with suffix like 10s or ISO8601 duration as described in {@link java.time.Duration#parse(CharSequence)}, for example PT10S.
     */
    String lockAtLeast();

    /**
     * @return Can be either time with suffix like 10s or ISO8601 duration as described in {@link java.time.Duration#parse(CharSequence)}, for example PT10S.
     */
    String lockAtMost();
}
