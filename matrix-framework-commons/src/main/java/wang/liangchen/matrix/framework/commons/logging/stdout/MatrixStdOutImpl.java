package wang.liangchen.matrix.framework.commons.logging.stdout;

import wang.liangchen.matrix.framework.commons.logging.AbstractMatrixLogger;
import wang.liangchen.matrix.framework.commons.logging.MatrixLogger;

/**
 * @author Liangchen.Wang 2022-06-20 14:51
 */
public class MatrixStdOutImpl extends AbstractMatrixLogger implements MatrixLogger {

    public MatrixStdOutImpl(String className) {
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
    public void error(String s, Throwable e) {
        System.err.println(s);
        e.printStackTrace(System.err);
    }

    @Override
    public void error(String s) {
        System.err.println(s);
    }

    @Override
    public void debug(String s) {
        System.out.println(s);
    }

    @Override
    public void trace(String s) {
        System.out.println(s);
    }

    @Override
    public void warn(String s) {
        System.out.println(s);
    }
}
