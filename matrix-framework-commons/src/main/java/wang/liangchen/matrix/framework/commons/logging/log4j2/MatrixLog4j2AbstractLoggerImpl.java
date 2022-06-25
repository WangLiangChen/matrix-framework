package wang.liangchen.matrix.framework.commons.logging.log4j2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;
import wang.liangchen.matrix.framework.commons.logging.MatrixLogger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLoggerFactory;

/**
 * @author Liangchen.Wang 2022-06-20 14:28
 */
public class MatrixLog4j2AbstractLoggerImpl implements MatrixLogger {
    private static final Marker MARKER = MarkerManager.getMarker(MatrixLoggerFactory.MARKER);
    private static final String FQCN = MatrixLog4j2Impl.class.getName();

    private final ExtendedLoggerWrapper nativeLogger;

    public MatrixLog4j2AbstractLoggerImpl(AbstractLogger nativeLogger) {
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
    public void error(String message, Throwable e) {
        nativeLogger.logIfEnabled(FQCN, Level.ERROR, MARKER, (Message) new SimpleMessage(message), e);
    }

    @Override
    public void error(String message) {
        error(message, null);
    }

    @Override
    public void debug(String message) {
        nativeLogger.logIfEnabled(FQCN, Level.DEBUG, MARKER, (Message) new SimpleMessage(message), null);
    }

    @Override
    public void trace(String message) {
        nativeLogger.logIfEnabled(FQCN, Level.TRACE, MARKER, (Message) new SimpleMessage(message), null);
    }

    @Override
    public void warn(String message) {
        nativeLogger.logIfEnabled(FQCN, Level.WARN, MARKER, (Message) new SimpleMessage(message), null);
    }
}
