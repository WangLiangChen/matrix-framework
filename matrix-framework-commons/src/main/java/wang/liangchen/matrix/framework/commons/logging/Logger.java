package wang.liangchen.matrix.framework.commons.logging;

/**
 * @author Liangchen.Wang 2022-06-20 11:21
 */
public interface Logger {

    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void error(Throwable e, String message, Object... args);

    void error(String message, Object... args);

    void debug(String message, Object... args);

    void trace(String message, Object... args);

    void warn(String message, Object... args);
}
