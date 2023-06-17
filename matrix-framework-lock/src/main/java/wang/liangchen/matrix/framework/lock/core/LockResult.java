package wang.liangchen.matrix.framework.lock.core;

/**
 * @author Liangchen.Wang 2022-08-24 23:09
 */
public class LockResult<R> {
    private final R object;
    private final boolean skipped;

    public LockResult(boolean skipped, R object) {
        this.skipped = skipped;
        this.object = object;
    }

    public static <R> LockResult<R> newInstance(R object) {
        return new LockResult<>(false, object);
    }

    public static <R> LockResult<R> skipped() {
        return new LockResult<>(true, null);
    }

    public R getObject() {
        return object;
    }

    public boolean isSkipped() {
        return skipped;
    }
}
