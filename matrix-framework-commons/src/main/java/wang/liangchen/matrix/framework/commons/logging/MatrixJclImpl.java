package wang.liangchen.matrix.framework.commons.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

/**
 * @author Liangchen.Wang 2022-06-20 14:19
 */
class MatrixJclImpl extends AbstractMatrixLogger implements MatrixLogger {
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
    public void error(Throwable e,String message, Object ... args) {
        nativeLogger.error(StringUtil.INSTANCE.format(message,args), e);
    }

    @Override
    public void error(String message,Object ... args) {
        nativeLogger.error(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void debug(String message,Object ... args) {
        nativeLogger.debug(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void trace(String message,Object ... args) {
        nativeLogger.trace(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void warn(String message,Object ... args) {
        nativeLogger.warn(StringUtil.INSTANCE.format(message,args));
    }
}
