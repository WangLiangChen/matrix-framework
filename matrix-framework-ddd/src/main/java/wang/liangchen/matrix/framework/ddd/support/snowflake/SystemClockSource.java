package wang.liangchen.matrix.framework.ddd.support.snowflake;

/**
 * 系统时钟时间源：直接使用{@link System#currentTimeMillis()}，无任何资源需释放。
 *
 * @author Liangchen.Wang
 */
public final class SystemClockSource implements ClockSource {

    public static final SystemClockSource INSTANCE = new SystemClockSource();

    private SystemClockSource() {
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
