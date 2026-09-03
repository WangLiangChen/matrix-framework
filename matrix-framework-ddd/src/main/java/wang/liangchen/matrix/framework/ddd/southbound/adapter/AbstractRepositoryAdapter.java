package wang.liangchen.matrix.framework.ddd.southbound.adapter;

import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.framework.ddd.processor.IPropertyProcessor;
import wang.liangchen.matrix.framework.ddd.processor.PropertyProcessorRegistry;
import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
        PO> implements IRepositoryAdapter, IRepositoryPort<ID, ROOT> {

    private static final ConcurrentMap<Class<?>, IPropertyProcessor> IDENTITY_PROCESSOR_CACHE = new ConcurrentHashMap<>();

    protected abstract Optional<PO> doFindById(ID id);

    protected abstract void doSave(PO po);

    protected abstract void doRemoveById(ID id);

    protected abstract ROOT reconstitute(PO po);

    protected abstract PO toPo(ROOT root);

    @Override
    public Optional<ROOT> findById(ID id) {
        return doFindById(id).map(this::reconstitute);
    }

    @Override
    public void save(ROOT root) {
        doSave(toPo(root));
    }

    @Override
    public void remove(ROOT root) {
        doRemoveById(extractId(root));
    }

    /**
     * 从聚合根提取身份标识：沿聚合根类的继承链定位唯一的@Identity字段（含继承字段），
     */
    @SuppressWarnings("unchecked")
    protected ID extractId(ROOT root) {
        IPropertyProcessor processor = IDENTITY_PROCESSOR_CACHE.computeIfAbsent(root.getClass(), this::resolveIdentityProcessor);
        return (ID) processor.getValue(root);
    }

    private IPropertyProcessor resolveIdentityProcessor(Class<?> rootClass) {
        List<IPropertyProcessor> processors = PropertyProcessorRegistry.getProcessors(rootClass);
        for (IPropertyProcessor processor : processors) {
            if (processor.getAnnotations().stream().anyMatch(annotation -> annotation.annotationType() == Identity.class)) {
                return processor;
            }
        }
        throw new IllegalStateException("No @Identity field found in " + rootClass.getName());
    }
}

