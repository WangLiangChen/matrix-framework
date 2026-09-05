package wang.liangchen.matrix.framework.ddd.support.snowflake;

/**
 * Snowflake时间戳溢出异常：当经过的时间超过41位上限时抛出。
 *
 * @author Liangchen.Wang
 */
public class SnowflakeOverflowException extends RuntimeException {

    public SnowflakeOverflowException(String message) {
        super(message);
    }

    public SnowflakeOverflowException(String message, Throwable cause) {
        super(message, cause);
    }
}