package wang.liangchen.matrix.framework.data.util;


import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LiangChen.Wang at 2021/2/1 8:26
 * 设计模式：原型模式，缓存并clone对象
 */
public enum EntityPrototypeUtil {
    // instance
    INSTANCE;
    private Map<String, RootEntity> entities = new ConcurrentHashMap<>();

    public <T> T clone(Class<T> clazz) {
        Assert.INSTANCE.notNull(clazz, "Parameter 'clazz' cannot be null");
        return clone(clazz.getName());
    }

    public <T> T clone(String className) {
        Assert.INSTANCE.notBlank(className, "Parameter 'className' cannot be null or blank");
        RootEntity rootEntity = loadPrototype(className);
        return ObjectUtil.INSTANCE.cast(rootEntity.clone());
    }

    private RootEntity loadPrototype(String className) {
        return entities.computeIfAbsent(className, key -> (RootEntity) ClassUtil.INSTANCE.instantiate(className));
    }
}
