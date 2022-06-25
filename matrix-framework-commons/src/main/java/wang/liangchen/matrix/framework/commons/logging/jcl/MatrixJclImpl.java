package wang.liangchen.matrix.framework.commons.logging.jcl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wang.liangchen.matrix.framework.commons.logging.AbstractMatrixLogger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLogger;

/**
 * @author Liangchen.Wang 2022-06-20 14:19
 */
public class MatrixJclImpl extends AbstractMatrixLogger implements MatrixLogger {
    private final Log nativeLogger;

    public MatrixJclImpl(String className) {
        super(className);
        this.nativeLogger = LogFactory.getLog(className);
    }


    @Override
    public boolean isDebugEnabled() {
        return nativeLogger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return nativeLogger.isTraceEnabled();
    }

    @Override
    public void error(String message, Throwable e) {
        nativeLogger.error(message, e);
    }

    @Override
    public void error(String message) {
        nativeLogger.error(message);
    }

    @Override
    public void debug(String message) {
        nativeLogger.debug(message);
    }

    @Override
    public void trace(String message) {
        nativeLogger.trace(message);
    }

    @Override
    public void warn(String message) {
        nativeLogger.warn(message);
    }
}
