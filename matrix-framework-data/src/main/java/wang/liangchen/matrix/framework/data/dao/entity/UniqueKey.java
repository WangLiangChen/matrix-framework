package wang.liangchen.matrix.framework.data.dao.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.encryption.DigestSignUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.DigestAlgorithm;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-07-08 9:49
 */
public abstract class UniqueKey implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String toKeyString() {
        List<Method> methods = ClassUtil.INSTANCE.declaredMethods(this.getClass());
        StringBuilder builder = new StringBuilder();
        // getter 排序
        methods.stream().map(Method::getName).sorted().forEach(methodName -> {
            if (methodName.startsWith(Symbol.GETTER_PREFIX.getSymbol())) {
                Object object = ClassUtil.INSTANCE.invokeGetter(this, methodName);
                builder.append(methodName).append(Symbol.EQUAL).append(object).append(Symbol.AND.getSymbol());
            }
        });
        logger.debug("The key of {} is {}", this.getClass(), builder);
        return DigestSignUtil.INSTANCE.digest(DigestAlgorithm.MD5, builder.toString());
    }
}
