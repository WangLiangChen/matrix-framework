package liangchen.wang.matrix.framework.commons.thread;

import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;

import java.util.concurrent.TimeUnit;

/**
 * @author Liangchen.Wang 2021-09-30 9:08
 */
public enum ThreadUtil {
    /**
     * instance
     */
    INSTANCE;

    public void sleep(TimeUnit timeUnit, long timeout) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            throw new MatrixErrorException(e);
        }
    }

    public void sleep(long timeoutMS) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeoutMS);
        } catch (InterruptedException e) {
            throw new MatrixErrorException(e);
        }
    }
}
