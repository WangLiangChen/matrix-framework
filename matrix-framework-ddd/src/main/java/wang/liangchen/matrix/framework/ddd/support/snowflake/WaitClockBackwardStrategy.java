package wang.liangchen.matrix.framework.ddd.support.snowflake;

/**
 * 等待时钟回拨恢复策略。
 * 当回拨量不超过容忍阈值时，自旋等待时钟追回；超过阈值则抛出异常。
 * 默认容忍阈值为5ms，适用于chrony平滑校时场景下的小幅回拨。
 * <p>
 * 等待时使用{@link Snowflake}传入的统一时间源（{@link ClockSource}），
 * 与ID生成使用同一时钟，避免多时间源不一致。
 *
 * @author Liangchen.Wang
 */
public class WaitClockBackwardStrategy implements ClockBackwardStrategy {

    private final long tolerateMillis;

    public WaitClockBackwardStrategy() {
        this(5L);
    }

    public WaitClockBackwardStrategy(long tolerateMillis) {
        if (tolerateMillis <= 0) {
            throw new IllegalArgumentException("tolerateMillis must be positive, got: " + tolerateMillis);
        }
        this.tolerateMillis = tolerateMillis;
    }

    @Override
    public long onBackward(long lastTimestamp, long currentTimestamp, ClockSource clockSource) {
        long offset = lastTimestamp - currentTimestamp;
        if (offset > tolerateMillis) {
            throw new ClockBackwardException(
                    "Clock backward exceeded tolerate threshold: " + offset + "ms > " + tolerateMillis + "ms");
        }
        long timestamp = clockSource.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            Thread.onSpinWait();
            timestamp = clockSource.currentTimeMillis();
        }
        return timestamp;
    }
}
