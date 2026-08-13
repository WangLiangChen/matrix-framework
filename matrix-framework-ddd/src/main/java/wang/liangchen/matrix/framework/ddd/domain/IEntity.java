package wang.liangchen.matrix.framework.ddd.domain;

import java.io.Serializable;

/**
 * @author Liangchen.Wang 2022-11-25 15:23
 * Marker interface for a Entity that is part of an Aggregate.
 */
public interface IEntity<ID extends Serializable> extends Serializable {
}
