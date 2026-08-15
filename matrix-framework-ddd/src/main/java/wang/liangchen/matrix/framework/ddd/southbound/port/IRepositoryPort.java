package wang.liangchen.matrix.framework.ddd.southbound.port;

import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;

import java.util.Optional;

/**
 * @author Liangchen.Wang
 * 领域仓储端口：一个聚合对应一个仓储，以聚合根为读写单位。
 * 仓储接口定义在领域层（端口），实现位于南向适配层（IRepositoryAdapter），依赖倒置。
 * 方法命名遵循统一语言（findById/save/remove）；变更方法只接收聚合根，不暴露内部实体的独立持久化接口，不泄漏PO/DO等基础设施类型。
 * 查询职责默认经本端口：findById 与业务扩展的统一语言命名查询方法返回聚合根，查询用例只读使用；本端口方法只返回聚合根（资源库返回领域对象），
 * 必要时 CQRS 的读模型/DTO 查询由业务自定义端口或应用层查询组件承载，经视图(IView)返回。
 */
@Port(PortType.Repository)
public interface IRepositoryPort<ID extends IIdentity, AR extends IAggregateRoot<ID>> extends IPort {

    /**
     * 按身份标识查找聚合根，不存在时返回Optional.empty()。
     */
    Optional<AR> findById(ID id);

    /**
     * 保存聚合根。
     */
    void save(AR aggregateRoot);

    /**
     * 删除聚合根。
     */
    void remove(AR aggregateRoot);

    /**
     * 按身份标识删除聚合根（默认经findById定位后委托remove(AR)）。
     */
    default void remove(ID id) {
        findById(id).ifPresent(this::remove);
    }
}
