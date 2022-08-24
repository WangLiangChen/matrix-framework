package wang.liangchen.matrix.framework.lock.core;

import java.util.function.Supplier;

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

    public static <R> TaskResult<R> newInstance(Supplier<R> task) {
        return new TaskResult<>(false, task.get());
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
