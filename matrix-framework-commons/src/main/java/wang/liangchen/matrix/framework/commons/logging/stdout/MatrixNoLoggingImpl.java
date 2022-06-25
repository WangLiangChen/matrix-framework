package wang.liangchen.matrix.framework.commons.logging.stdout;

import wang.liangchen.matrix.framework.commons.logging.AbstractMatrixLogger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLogger;

/**
 * @author Liangchen.Wang 2022-06-20 14:53
 */
public class MatrixNoLoggingImpl extends AbstractMatrixLogger implements MatrixLogger {
    public MatrixNoLoggingImpl(String className) {
        super(className);
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void error(String message, Throwable e) {

    }

    @Override
    public void error(String message) {

    }

    @Override
    public void debug(String message) {

    }

    @Override
    public void trace(String message) {

    }

    @Override
    public void warn(String message) {

    }
}
