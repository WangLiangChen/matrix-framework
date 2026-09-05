package wang.liangchen.matrix.framework.ddd.support.snowflake;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 原生Snowflake ID生成器。
 * <p>
 * 64位ID结构（标准Twitter Snowflake）：
 * <pre>
 * +-----------+-------------------------------------------------------+------------------+------------------+
 * | 1 Bit Sign|                   41 Bit Timestamp                     | 10 Bit WorkerId  | 12 Bit Sequence  |
 * +-----------+-------------------------------------------------------+------------------+------------------+
 * </pre>
 * <ul>
 *   <li>1位符号位，始终为0</li>
 *   <li>41位时间戳（毫秒精度，自epoch起约69年）</li>
 *   <li>10位workerId（0~1023），由{@link WorkerIdStrategy}决定</li>
 *   <li>12位序列号（0~4095，每毫秒每worker可生成约4000个ID）</li>
 * </ul>
 * <p>
 * 特性：
 * <ul>
 *   <li>workerId分配策略可定制（IP低位、固定值、K8s Pod序号等）</li>
 *   <li>时钟回拨处理策略可定制（5ms等待、抛异常、借用未来时间等）</li>
 *   <li>时间源可定制（{@link ClockSource}）：系统时钟或chrony平滑校时时钟</li>
 *   <li>每毫秒序列号从随机值起始，避免低位连续可预测</li>
 * </ul>
 * <p>
 * <b>单实例约束</b>：一个workerId在进程内只能对应一个活跃实例。
 * 每个实例独立维护lastTimestamp与sequence，同workerId的两个实例在同一毫秒内
 * 会生成重复ID（随机序列起始仅提供[0,100)的初始偏移，无法避免碰撞）。
 * 规范用法是通过{@code SnowflakeIdentity.next()}使用的全局单例生成器；
 * 自行构建实例的场景（多workerId分片等）必须保证workerId互不重叠。
 * <p>
 * 资源释放：{@link #close()}仅关闭由Builder默认创建、归本生成器所有的时钟源；
 * 注入的时钟源由调用方管理（可能被多个生成器共享），不会被关闭。
 * 注意：Snowflake 不实现{@link java.lang.AutoCloseable}，
 * 因为通常作为全局单例使用，生命周期等同 JVM 进程，
 * 且默认时钟源的 daemon 线程在 JVM 退出时自动清理；
 * 频繁创建销毁的场景才需主动调用 close()。
 *
 * @author Liangchen.Wang
 */
public class Snowflake {

    private static final long SEQUENCE_BITS = 12L;
    private static final long WORKER_ID_BITS = 10L;
    private static final long TIMESTAMP_BITS = 41L;

    /**
     * workerId上限（10位全1）。
     */
    public static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private static final long MAX_TIMESTAMP = ~(-1L << TIMESTAMP_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 新毫秒序列号随机起始的上界（不含）。
     * 每毫秒序列号从[0, 100)随机起始，溢出时跳到下一毫秒，
     * 避免序列号恒从0开始的连续可预测性，代价是每毫秒最多减少99个ID的生成能力（最少仍可生成3997个）。
     */
    private static final long RANDOM_SEQUENCE_BOUND = 100L;

    private final long epoch;
    private final long workerId;
    private final ClockBackwardStrategy clockBackwardStrategy;
    private final ClockSource clockSource;
    /**
     * 时钟源所有权：仅当时钟源由Builder默认创建时为true。
     * 注入的时钟源可能被多个生成器共享或由调用方管理，close()时不释放。
     */
    private final boolean ownsClockSource;

    private long lastTimestamp = -1L;
    private long sequence = 0L;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private Snowflake(long epoch, long workerId,
                      ClockBackwardStrategy clockBackwardStrategy,
                      ClockSource clockSource, boolean ownsClockSource) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                    "workerId must be between 0 and " + MAX_WORKER_ID + ", got: " + workerId);
        }
        this.epoch = epoch;
        this.workerId = workerId;
        this.clockBackwardStrategy = clockBackwardStrategy;
        this.clockSource = clockSource;
        this.ownsClockSource = ownsClockSource;
    }

    /**
     * 生成下一个Snowflake ID（线程安全）。
     *
     * @return 64位Snowflake ID
     */
    public synchronized long nextId() {
        long timestamp = clockSource.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            timestamp = clockBackwardStrategy.onBackward(lastTimestamp, timestamp, clockSource);
            if (timestamp < lastTimestamp) {
                // 防御：策略返回值不满足"不早于lastTimestamp"的约定时兜底
                timestamp = tilNextMillis(lastTimestamp);
            }
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = ThreadLocalRandom.current().nextLong(RANDOM_SEQUENCE_BOUND);
        }

        lastTimestamp = timestamp;

        long elapsed = timestamp - epoch;
        if (elapsed < 0 || elapsed > MAX_TIMESTAMP) {
            throw new SnowflakeOverflowException(
                    "Snowflake timestamp out of range: elapsed=" + elapsed
                            + " must be in [0, " + MAX_TIMESTAMP + "], check epoch configuration");
        }

        return (elapsed << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 从ID中反解出生成时刻的时间戳（ms，绝对时间，即epoch+ID中的相对时间）。
     *
     * @param id Snowflake ID
     * @return 生成时刻的绝对时间戳（ms）
     */
    public long extractTimestamp(long id) {
        return epoch + (id >> TIMESTAMP_SHIFT);
    }

    /**
     * 从ID中反解出workerId。
     *
     * @param id Snowflake ID
     * @return workerId（0~1023）
     */
    public static long extractWorkerId(long id) {
        return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }

    /**
     * 从ID中反解出序列号。
     *
     * @param id Snowflake ID
     * @return sequence（0~4095）
     */
    public static long extractSequence(long id) {
        return id & MAX_SEQUENCE;
    }

    /**
     * 释放生成器持有的资源：仅当时钟源为Builder默认创建（归本生成器所有）时关闭它；
     * 注入的时钟源由调用方管理（可能被多个生成器共享），不会被关闭。
     * <p>
     * close()后生成器仍可继续生成ID（时间跟随系统时钟，仅平滑校准冻结在最后的偏移量上）；
     * close()幂等（CAS保证仅执行一次）、可与其他线程的nextId()并发调用。
     * 全局单例典型用法无需调用本方法（守护线程随JVM退出自动回收），
     * 本方法用于提前释放（如应用停机钩子）。
     */
    public void close() {
        if (closed.compareAndSet(false, true) && ownsClockSource) {
            clockSource.close();
        }
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = clockSource.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            Thread.onSpinWait();
            timestamp = clockSource.currentTimeMillis();
        }
        return timestamp;
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getEpoch() {
        return epoch;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Snowflake构建器，默认使用以下策略：
     * <ul>
     *   <li>workerId: {@link IpWorkerIdStrategy}（基于IPv4低16位，多网卡/K8s环境建议替换为{@link FixedWorkerIdStrategy}）</li>
     *   <li>时钟回拨: {@link WaitClockBackwardStrategy}（容忍5ms回拨）</li>
     *   <li>时间源: 懒创建{@link ChronyClockCalibrator}（仅在{@link #build()}且未显式设置时创建）</li>
     * </ul>
     */
    public static class Builder {

        private static final LocalDateTime DEFAULT_EPOCH = LocalDateTime.of(2026, 9, 5, 0, 0, 0);

        private long epoch;
        private WorkerIdStrategy workerIdStrategy;
        private ClockBackwardStrategy clockBackwardStrategy;
        /**
         * 懒创建：仅在{@link #build()}且未显式设置时才创建默认chrony校准器，
         * 避免构建器被覆盖设置或放弃时泄漏后台校准线程。
         */
        private ClockSource clockSource;

        public Builder() {
            // epoch固定按UTC解析，保证跨时区部署的ID时间语义一致
            this.epoch = DEFAULT_EPOCH.toInstant(ZoneOffset.UTC).toEpochMilli();
            this.workerIdStrategy = new IpWorkerIdStrategy();
            this.clockBackwardStrategy = new WaitClockBackwardStrategy();
        }

        public Builder epoch(long epochMillis) {
            this.epoch = epochMillis;
            return this;
        }

        /**
         * 设置epoch（按UTC解析，保证跨时区部署一致）。
         *
         * @param epochDateTime epoch时间（UTC语义）
         * @return this
         */
        public Builder epoch(LocalDateTime epochDateTime) {
            this.epoch = epochDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            return this;
        }

        public Builder workerIdStrategy(WorkerIdStrategy workerIdStrategy) {
            this.workerIdStrategy = workerIdStrategy;
            return this;
        }

        public Builder clockBackwardStrategy(ClockBackwardStrategy clockBackwardStrategy) {
            this.clockBackwardStrategy = clockBackwardStrategy;
            return this;
        }

        /**
         * 设置时间源。注入的时间源由调用方管理生命周期：
         * {@link Snowflake#close()}不会关闭它（它可能被多个生成器共享），
         * 需要释放时请调用方自行关闭。
         *
         * @param clockSource 时间源
         * @return this
         */
        public Builder clockSource(ClockSource clockSource) {
            this.clockSource = clockSource;
            return this;
        }

        public Snowflake build() {
            long resolvedWorkerId = workerIdStrategy.resolveWorkerId();
            // 未显式设置时才创建默认时钟源，且其所有权归生成的Snowflake（close时释放）；
            // 注入的时钟源所有权归调用方，close()不会将其关闭
            boolean ownsClockSource = clockSource == null;
            ClockSource resolvedClockSource = ownsClockSource
                    ? new ChronyClockCalibrator()
                    : clockSource;
            return new Snowflake(epoch, resolvedWorkerId, clockBackwardStrategy,
                    resolvedClockSource, ownsClockSource);
        }
    }
}