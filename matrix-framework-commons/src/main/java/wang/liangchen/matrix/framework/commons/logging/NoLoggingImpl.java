package wang.liangchen.matrix.framework.commons.logging;

/**
 * @author Liangchen.Wang 2022-06-20 14:53
 */
class NoLoggingImpl extends AbstractLogger implements Logger {
    public NoLoggingImpl(String className) {
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
    public void error(Throwable e,String message,Object ... args) {

    }

    @Override
    public void error(String message,Object ... args) {

    }

    @Override
    public void debug(String message,Object ... args) {

    }

    @Override
    public void trace(String message,Object ... args) {

    }

    @Override
    public void warn(String message,Object ... args) {

    }
}
