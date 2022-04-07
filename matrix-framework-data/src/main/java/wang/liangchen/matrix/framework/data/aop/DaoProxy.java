package wang.liangchen.matrix.framework.data.aop;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Liangchen.Wang 2021-10-20 9:06
 */
public class DaoProxy<T> implements InvocationHandler, Serializable {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)  {
        try {
            return method.invoke(this, args);
        } catch (Throwable t) {
            throw new MatrixErrorException(t);
        }
    }
}