package liangchen.wang.matrix.framework.commons.object;

import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;

/**
 * @author Liangchen.Wang 2021-09-30 15:22
 */
public enum ClassUtil {
    /**
     * instance
     */
    INSTANCE;

    @SuppressWarnings("unchecked")
    public <T> T instantiate(String className) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(className);
            return instantiate(clazz);
        } catch (ClassNotFoundException e) {
            throw new MatrixErrorException(e);
        }
    }

    public <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }
}
