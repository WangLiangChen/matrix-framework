package wang.liangchen.matrix.framework.commons.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;
import wang.liangchen.matrix.framework.commons.StringUtil;

/**
 * @author Liangchen.Wang 2022-06-20 14:28
 */
class Log4j2ExtendedLoggerImpl implements Logger {
    private static final Marker MARKER = MarkerManager.getMarker(LoggerFactory.MARKER);
    private static final String FQCN = Log4j2Impl.class.getName();

    private final ExtendedLoggerWrapper nativeLogger;

    public Log4j2ExtendedLoggerImpl(AbstractLogger nativeLogger) {
        this.nativeLogger = new ExtendedLoggerWrapper(nativeLogger, nativeLogger.getName(), nativeLogger.getMessageFactory());
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
    public void error(Throwable e, String message, Object... args) {
        nativeLogger.logIfEnabled(FQCN, Level.ERROR, MARKER, (Message) new SimpleMessage(StringUtil.INSTANCE.format(message, args)), e);
    }

    @Override
    public void error(String message, Object... args) {
        error(null, message, args);
    }

    @Override
    public void debug(String message, Object... args) {
        nativeLogger.logIfEnabled(FQCN, Level.DEBUG, MARKER, (Message) new SimpleMessage(StringUtil.INSTANCE.format(message, args)), null);
    }

    @Override
    public void trace(String message, Object... args) {
        nativeLogger.logIfEnabled(FQCN, Level.TRACE, MARKER, (Message) new SimpleMessage(StringUtil.INSTANCE.format(message, args)), null);
    }

    @Override
    public void warn(String message, Object... args) {
        nativeLogger.logIfEnabled(FQCN, Level.WARN, MARKER, (Message) new SimpleMessage(StringUtil.INSTANCE.format(message, args)), null);
    }
}
