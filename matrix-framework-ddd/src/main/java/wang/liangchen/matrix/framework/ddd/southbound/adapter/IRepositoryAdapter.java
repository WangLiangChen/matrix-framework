package wang.liangchen.matrix.framework.ddd.southbound.adapter;

import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

/**
 * @author Liangchen.Wang
 * Marker interface
 * Mark a repository adapter
 */
@Adapter(PortType.Repository)
public interface IRepositoryAdapter<ID extends IIdentity, ROOT extends IAggregateRoot<ID>> extends IAdapter {
}
