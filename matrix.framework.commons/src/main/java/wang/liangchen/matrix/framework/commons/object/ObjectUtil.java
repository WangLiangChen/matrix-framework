package wang.liangchen.matrix.framework.commons.object;

import net.sf.cglib.beans.BeanCopier;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.utils.StringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liangchen.Wang 2021-09-30 15:22
 */
public enum ObjectUtil {
    /**
     * instance
     */
    INSTANCE;
    public Map<String, BeanCopier> beanCopiers = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T cast(Object obj) {
        if (null == obj) {
            return null;
        }
        return (T) obj;
    }

    public void copyProperties(Object source, Object target) {
        String copierKey = StringUtil.INSTANCE.format("{}-{}", source.getClass().getName(), target.getClass().getName());
        BeanCopier copier = beanCopiers.computeIfAbsent(copierKey, key -> BeanCopier.create(source.getClass(), target.getClass(), false));
        copier.copy(source, target, null);
    }


    public <T> T copyProperties(Object source, Class<T> targetClass) {
        String copierKey = StringUtil.INSTANCE.format("{}-{}", source.getClass().getName(), targetClass.getName());
        BeanCopier copier = beanCopiers.computeIfAbsent(copierKey, key -> BeanCopier.create(source.getClass(), targetClass, false));
        T target = ClassUtil.INSTANCE.instantiate(targetClass);
        copier.copy(source, target, null);
        return target;
    }
}
