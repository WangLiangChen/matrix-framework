package wang.liangchen.matrix.framework.commons.logging;

import wang.liangchen.matrix.framework.commons.string.StringUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Liangchen.Wang 2022-06-20 14:47
 */
class MatrixJulImpl extends AbstractMatrixLogger implements MatrixLogger {
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
    public void error(Throwable e,String message,Object ... args) {
        nativeLogger.log(Level.SEVERE, StringUtil.INSTANCE.format(message,args), e);
    }

    @Override
    public void error(String message,Object ... args) {
        nativeLogger.log(Level.SEVERE, StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void debug(String message,Object ... args) {
        nativeLogger.log(Level.FINE, StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void trace(String message,Object ... args) {
        nativeLogger.log(Level.FINER, StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void warn(String message,Object ... args) {
        nativeLogger.log(Level.WARNING, StringUtil.INSTANCE.format(message,args));
    }
}
