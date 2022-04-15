package wang.liangchen.matrix.framework.data.query;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.commons.object.BeanUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.type.TypeToken;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class CriteriaBuilder<T> {
    private final List<String> whereList = new ArrayList<>();
    private final List<Object> whereValueList = new ArrayList<>();
    private final static String AND = " and ";
    private final static String OR = " or ";

    public static <T> CriteriaBuilder<T> newInstance() {
        return new CriteriaBuilder<T>() {
        };
    }

    public CriteriaBuilder<T> eq(Getter<T> getter, Object value) {
        resolveColumnName(getter);
        return this;
    }


    private String resolveColumnName(Getter<T> getter) {
        SerializedLambda lambda = LambdaUtil.INSTANCE.serializedLambda(getter);
        String implClass = lambda.getImplClass();
        implClass= StringUtil.INSTANCE.Path2Package(implClass);
        Class<?> entityClass = ClassUtil.INSTANCE.forName(implClass);
        String implMethodName = lambda.getImplMethodName();
        String fieldName = BeanUtil.INSTANCE.resolveFieldName(implMethodName);

        return "fieldName";
    }


    @Override
    public String toString() {
        return whereList.stream().collect(Collectors.joining(AND));
    }
}
