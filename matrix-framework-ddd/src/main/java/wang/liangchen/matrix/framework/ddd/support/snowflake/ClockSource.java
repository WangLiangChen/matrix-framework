package wang.liangchen.matrix.framework.ddd.support.snowflake;

/**
 * 时间源抽象：Snowflake 及其策略（时钟回拨策略等）统一从此接口获取时间，
 * 避免直接耦合{@code System.currentTimeMillis()}导致多时间源不一致。
 * <p>
 * 实现：
 * <ul>
 *   <li>{@link SystemClockSource}：直接使用系统时钟</li>
 *   <li>{@link ChronyClockCalibrator}：基于chrony的平滑校准时钟</li>
 * </ul>
 * 自定义实现（如测试用的可控时钟）只需实现本接口，无需继承任何具体类。
 *
 * @author Liangchen.Wang
 */
@FunctionalInterface
public interface ClockSource extends AutoCloseable {

    /**
     * 获取当前时间戳（毫秒）。
     *
     * @return 当前时间戳（ms）
     */
    long currentTimeMillis();

    /**
     * 释放时间源持有的资源（如后台校准线程）。
     * 无状态实现（如{@link SystemClockSource}）无需任何处理。
     */
    @Override
    default void close() {
    }
}
