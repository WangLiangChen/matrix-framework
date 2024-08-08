package wang.liangchen.matrix.framework.commons.logging;


import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;
import wang.liangchen.matrix.framework.commons.StringUtil;

/**
 * @author Liangchen.Wang 2022-06-20 11:43
 */
class Slf4jImpl extends AbstractLogger implements Logger {

    private Logger logger;

    public Slf4jImpl(String className) {
        super(className);
        org.slf4j.Logger nativeLogger = LoggerFactory.getLogger(className);

        if (nativeLogger instanceof LocationAwareLogger) {
            try {
                // 判断是否有这个方法
                nativeLogger.getClass().getMethod("log", Marker.class, String.class, int.class, String.class, Object[].class, Throwable.class);
                logger = new Slf4JLocationAwareLoggerImpl((LocationAwareLogger) nativeLogger);
                return;
            } catch (SecurityException | NoSuchMethodException e) {
            }
        }
        logger = new Slf4jLoggerImpl(nativeLogger);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void error(Throwable e, String message, Object... args) {
        logger.error(StringUtil.INSTANCE.format(message, args), e);
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(StringUtil.INSTANCE.format(message, args));
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(StringUtil.INSTANCE.format(message, args));
    }

    @Override
    public void trace(String message, Object... args) {
        logger.trace(StringUtil.INSTANCE.format(message, args));
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(StringUtil.INSTANCE.format(message, args));
    }
}
