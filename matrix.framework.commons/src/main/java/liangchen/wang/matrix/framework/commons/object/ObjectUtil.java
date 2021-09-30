package liangchen.wang.matrix.framework.commons.object;

/**
 * @author Liangchen.Wang 2021-09-30 15:22
 */
public enum ObjectUtil {
    /**
     * instance
     */
    INSTANCE;
    @SuppressWarnings("unchecked")
    public <T> T cast(Object obj) {
        if (null == obj) {
            return null;
        }
        return (T) obj;
    }
}
