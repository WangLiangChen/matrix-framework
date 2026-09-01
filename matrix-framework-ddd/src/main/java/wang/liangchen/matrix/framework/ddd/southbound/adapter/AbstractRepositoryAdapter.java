package wang.liangchen.matrix.framework.ddd.southbound.adapter;

import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;

import java.util.Optional;

/**
 * 仓储适配器抽象基类：提供findById/save/remove的通用骨架，消除各业务适配器的样板代码。
 * 子类只需实现doFindById/doSave/doRemoveById三个数据访问原语，
 * 以及reconstitute/toPo两个防腐翻译方法，即可自动获得findById/save/remove的完整行为；
 * remove的ID提取由extractId模板方法完成（默认通过@Identity注解反射提取，子类可覆写）。
 *
 * @param <ID>   聚合根身份标识类型
 * @param <ROOT> 聚合根类型
 * @param <PO>   持久化对象类型
 * @author Liangchen.Wang
 */
public abstract class AbstractRepositoryAdapter<
        ID extends IIdentity,
        ROOT extends IAggregateRoot<ID>,
        PO> implements IRepositoryAdapter {

    protected abstract Optional<PO> doFindById(ID id);

    protected abstract void doSave(PO po);

    protected abstract void doRemoveById(ID id);

    protected abstract ROOT reconstitute(PO po);

    protected abstract PO toPo(ROOT root);

    public Optional<ROOT> findById(ID id) {
        return doFindById(id).map(this::reconstitute);
    }

    public void save(ROOT root) {
        doSave(toPo(root));
    }

    public void remove(ROOT root) {
        doRemoveById(extractId(root));
    }

    /**
     * 从聚合根提取身份标识：默认通过@Identity注解反射提取，
     * 子类可在身份标识字段命名与注解位置不标准时覆写此方法。
     */
    @SuppressWarnings("unchecked")
    protected ID extractId(ROOT root) {
        for (java.lang.reflect.Field field : root.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(wang.liangchen.matrix.framework.ddd.domain.identity.Identity.class)) {
                try {
                    field.setAccessible(true);
                    return (ID) field.get(root);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to extract identity from " + root.getClass().getName(), e);
                }
            }
        }
        throw new IllegalStateException("No @Identity field found in " + root.getClass().getName());
    }
}