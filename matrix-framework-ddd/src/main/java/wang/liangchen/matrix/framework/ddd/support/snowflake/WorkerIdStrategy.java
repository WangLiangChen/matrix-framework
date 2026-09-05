package wang.liangchen.matrix.framework.ddd.support.snowflake;

/**
 * workerId分配策略接口。
 * Snowflake的10位workerId（0~1023）由具体策略决定来源，
 * 如基于IP低位、基于数据中心+机器编号、固定值等。
 *
 * @author Liangchen.Wang
 */
@FunctionalInterface
public interface WorkerIdStrategy {

    long resolveWorkerId();
}