package wang.liangchen.matrix.framework.ddd.support.snowflake;

/**
 * 固定workerId策略：直接使用指定的workerId值。
 * 适用于容器化环境（K8s Pod序号等）或单机调试场景。
 *
 * @author Liangchen.Wang
 */
public class FixedWorkerIdStrategy implements WorkerIdStrategy {

    private final long workerId;

    public FixedWorkerIdStrategy(long workerId) {
        if (workerId < 0 || workerId > Snowflake.MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                    "workerId must be between 0 and " + Snowflake.MAX_WORKER_ID + ", got: " + workerId);
        }
        this.workerId = workerId;
    }

    @Override
    public long resolveWorkerId() {
        return workerId;
    }
}