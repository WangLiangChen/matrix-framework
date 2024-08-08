package wang.liangchen.matrix.framework.commons.logging;

import wang.liangchen.matrix.framework.commons.StringUtil;

/**
 * @author Liangchen.Wang 2022-06-20 14:51
 */
class StdOutImpl extends AbstractLogger implements Logger {

    public StdOutImpl(String className) {
        super(className);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(Throwable e,String message,Object ... args) {
        System.err.println(StringUtil.INSTANCE.format(message,args));
        e.printStackTrace(System.err);
    }

    @Override
    public void error(String message,Object ... args) {
        System.err.println(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void debug(String message,Object ... args) {
        System.out.println(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void trace(String message,Object ... args) {
        System.out.println(StringUtil.INSTANCE.format(message,args));
    }

    @Override
    public void warn(String message,Object ... args) {
        System.out.println(StringUtil.INSTANCE.format(message,args));
    }
}
