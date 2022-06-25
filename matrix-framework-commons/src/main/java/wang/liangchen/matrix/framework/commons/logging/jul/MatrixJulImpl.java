package wang.liangchen.matrix.framework.commons.logging.jul;

import wang.liangchen.matrix.framework.commons.logging.AbstractMatrixLogger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Liangchen.Wang 2022-06-20 14:47
 */
public class MatrixJulImpl extends AbstractMatrixLogger implements MatrixLogger {
    private final Logger nativeLogger;

    public MatrixJulImpl(String className) {
        super(className);
        this.nativeLogger = Logger.getLogger(className);
    }

    @Override
    public boolean isDebugEnabled() {
        return nativeLogger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isTraceEnabled() {
        return nativeLogger.isLoggable(Level.FINER);
    }

    @Override
    public void error(String s, Throwable e) {
        nativeLogger.log(Level.SEVERE, s, e);
    }

    @Override
    public void error(String s) {
        nativeLogger.log(Level.SEVERE, s);
    }

    @Override
    public void debug(String s) {
        nativeLogger.log(Level.FINE, s);
    }

    @Override
    public void trace(String s) {
        nativeLogger.log(Level.FINER, s);
    }

    @Override
    public void warn(String s) {
        nativeLogger.log(Level.WARNING, s);
    }
}
