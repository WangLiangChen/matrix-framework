package liangchen.wang.matrix.framework.data.aop;

import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;
import org.apache.ibatis.reflection.ExceptionUtil;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Liangchen.Wang 2021-10-20 9:06
 */
public class DaoProxy<T> implements InvocationHandler, Serializable {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else {
                //return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
                return method.invoke(this, args);
            }
        } catch (Throwable t) {
            throw new MatrixErrorException(t);
        }
    }
}
