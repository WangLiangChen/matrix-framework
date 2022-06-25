package wang.liangchen.matrix.framework.commons.logging;

/**
 * @author Liangchen.Wang 2022-06-20 11:21
 */
public interface MatrixLogger {

    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void error(String message, Throwable e);

    void error(String message);

    void debug(String message);

    void trace(String message);

    void warn(String message);
}
