package wang.liangchen.matrix.framework.lock.core;

/**
 * @author Liangchen.Wang 2022-08-24 23:09
 */
public class TaskResult<R> {
    private final R object;
    private final boolean skipped;

    public TaskResult(boolean skipped, R object) {
        this.skipped = skipped;
        this.object = object;
    }

    public static <R> TaskResult<R> newInstance(R object) {
        return new TaskResult<>(false, object);
    }

    public static <R> TaskResult<R> skipped() {
        return new TaskResult<>(true, null);
    }

    public R getObject() {
        return object;
    }

    public boolean isSkipped() {
        return skipped;
    }
}
