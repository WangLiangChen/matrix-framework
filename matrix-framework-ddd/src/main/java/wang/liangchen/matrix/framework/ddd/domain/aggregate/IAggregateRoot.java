package wang.liangchen.matrix.framework.ddd.domain.aggregate;

import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;

/**
 * @author Liangchen.Wang
 * Marker interface
 * Mark a Root Entity of an Aggregate
 */
public interface IAggregateRoot<ID extends IIdentity> extends IEntity<ID> {
}
