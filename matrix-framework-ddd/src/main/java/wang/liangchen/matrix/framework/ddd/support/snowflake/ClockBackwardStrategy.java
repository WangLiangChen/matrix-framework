package wang.liangchen.matrix.framework.ddd.support.snowflake;

/**
 * 时钟回拨处理策略接口。
 * 当检测到当前时间小于上一次生成ID的时间时，由具体策略决定如何应对，
 * 并返回一个可继续生成ID的时间戳。
 * 常见策略：等待回拨恢复（{@link WaitClockBackwardStrategy}）、抛出异常、借用未来时间等。
 *
 * @author Liangchen.Wang
 */
@FunctionalInterface
public interface ClockBackwardStrategy {

    /**
     * 处理时钟回拨。
     * <p>
     * 约定：返回值必须是可继续生成ID的时间戳（>= lastTimestamp）。
     * 例如等待策略返回追平后的真实时间；借用未来时间策略返回 lastTimestamp（或再+1ms）；
     * 抛异常策略直接抛出而不返回。
     * 调用方（{@link Snowflake}）对不满足约定的返回值会兜底等待到下一毫秒。
     *
     * @param lastTimestamp    上一次生成ID的时间戳（ms）
     * @param currentTimestamp 当前时间戳（ms），小于lastTimestamp
     * @param clockSource      统一时间源，策略内部等待/重读时间必须使用它，禁止直接耦合系统时钟
     * @return 校正后的可用时间戳（ms），必须不小于lastTimestamp
     */
    long onBackward(long lastTimestamp, long currentTimestamp, ClockSource clockSource);
}
