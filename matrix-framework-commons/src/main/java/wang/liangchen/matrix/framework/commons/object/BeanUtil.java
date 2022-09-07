package wang.liangchen.matrix.framework.commons.object;

import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

/**
 * @author Liangchen.Wang 2022-04-15 11:36
 */
public enum BeanUtil {
    /**
     * instance
     */
    INSTANCE;
    private final static String SET = "set";
    private final static String GET = "get";
    private final static String IS = "is";

    public String resolveFieldName(String methodName) {
        if (methodName.startsWith(GET) || methodName.startsWith(SET)) {
            return StringUtil.INSTANCE.firstLetterLowerCase(methodName.substring(3));
        }
        if (methodName.startsWith(IS)) {
            return StringUtil.INSTANCE.firstLetterLowerCase(methodName.substring(2));
        }
        throw new MatrixWarnException("methodName is illegal");
    }

    public String resolveGetterName(String fieldName) {
        return String.format("%s%s", GET, StringUtil.INSTANCE.firstLetterUpperCase(fieldName));
    }

    public String resolveSetterName(String fieldName) {
        return String.format("%s%s", SET, StringUtil.INSTANCE.firstLetterUpperCase(fieldName));
    }

    public String resolveIsName(String fieldName) {
        return String.format("%s%s", IS, StringUtil.INSTANCE.firstLetterUpperCase(fieldName));
    }
}
