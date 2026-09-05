package wang.liangchen.matrix.framework.ddd.support.snowflake;

/**
 * 时钟回拨异常：当时钟回拨量超过容忍阈值时抛出。
 *
 * @author Liangchen.Wang
 */
public class ClockBackwardException extends RuntimeException {

    public ClockBackwardException(String message) {
        super(message);
    }

    public ClockBackwardException(String message, Throwable cause) {
        super(message, cause);
    }
}