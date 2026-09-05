package wang.liangchen.matrix.framework.ddd.domain.identity;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.support.snowflake.Snowflake;

import java.util.Objects;

/**
 * 通用Snowflake身份标识：系统生成、无业务含义的代理标识（Long值）。
 * 不变类：final类、值在构造时确定，通过静态工厂of/next创建；值相等语义继承自AbstractValueObject。
 * <p>
 * 默认使用标准Snowflake算法（1+41+10+12位），可通过{@link #initialize(Snowflake)}定制生成器。
 * <p>
 * <b>全局单例</b>：本类持有进程内唯一的默认生成器，{@link #next()}是生成Snowflake ID的规范入口；
 * 应用一般应通过它取号，而非自行new生成器（同workerId多实例会产生重复ID，见{@link Snowflake}类说明）。
 * <p>
 * 默认生成器采用惰性初始化：首次{@link #next()}时才解析网络环境并启动时钟校准，
 * 避免 {@link ExceptionInInitializerError} 使类永久不可用，且初始化失败可重试。
 * {@link #initialize(Snowflake)}与{@link #shutdown()}与惰性初始化串行执行，无丢失更新。
 *
 * @author Liangchen.Wang
 */
@DomainModel(DomainMetaModel.Identity)
public final class SnowflakeIdentity extends AbstractSimpleIdentity<Long> implements ILongIdentity {

    private static volatile Snowflake snowflake;

    private final long value;

    private SnowflakeIdentity(long value) {
        this.value = value;
    }

    /**
     * 将身份标识的值转换为身份标识对象。
     */
    public static SnowflakeIdentity of(long value) {
        return new SnowflakeIdentity(value);
    }

    /**
     * 生成新的Snowflake身份标识。
     */
    public static SnowflakeIdentity next() {
        return new SnowflakeIdentity(generator().nextId());
    }

    /**
     * 替换底层Snowflake生成器（如需自定义epoch、workerId策略、时钟回拨策略等）。
     * <p>
     * 线程安全：与惰性初始化、{@link #shutdown()}共用同一把类锁串行执行。
     * 必须加锁而非仅volatile写：若本方法与首次惰性初始化并发，
     * 无锁写入会被随后完成的默认初始化静默覆盖（丢失更新）。
     * <p>
     * 本方法仅替换引用，<b>不会关闭</b>被替换的旧生成器——旧生成器可能仍被其他线程使用，
     * 其时钟源也可能与新生成器共享，自动关闭会引发静默故障。
     * 调用方若需释放旧生成器，请对返回值显式调用{@code close()}：
     * <pre>{@code
     * Snowflake old = SnowflakeIdentity.initialize(newGenerator);
     * if (old != null) {
     *     old.close();
     * }
     * }</pre>
     * <p>
     * 注意：运行期间替换生成器会使前后ID来自不同生成器（时间戳、workerId语义可能跳变），
     * 建议仅在启动阶段或流量静默期调用。
     *
     * @param generator 新的Snowflake生成器实例
     * @return 被替换的旧生成器；此前不存在时返回null
     */
    public static Snowflake initialize(Snowflake generator) {
        Objects.requireNonNull(generator, "generator must not be null");
        synchronized (SnowflakeIdentity.class) {
            Snowflake old = SnowflakeIdentity.snowflake;
            SnowflakeIdentity.snowflake = generator;
            return old;
        }
    }

    /**
     * 关闭并移除当前生成器（若存在），用于应用停机时显式释放校准线程；
     * 此后首次{@link #next()}将按默认配置重新创建生成器。
     * 非必须操作：校准线程为守护线程，随JVM退出自动回收。
     */
    public static void shutdown() {
        synchronized (SnowflakeIdentity.class) {
            Snowflake current = snowflake;
            snowflake = null;
            if (current != null) {
                current.close();
            }
        }
    }

    /**
     * 获取当前Snowflake生成器（不存在时按默认配置创建，会触发完整初始化：
     * 网卡解析与chrony时钟校准启动，仅用于读取状态时请注意此副作用）。
     */
    public static Snowflake getSnowflake() {
        return generator();
    }

    private static Snowflake generator() {
        // 双重检查锁惰性初始化：初始化失败不会永久破坏类（区别于静态初始化器）
        Snowflake result = snowflake;
        if (result == null) {
            synchronized (SnowflakeIdentity.class) {
                result = snowflake;
                if (result == null) {
                    result = Snowflake.builder().build();
                    snowflake = result;
                }
            }
        }
        return result;
    }

    @Override
    public Long value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
