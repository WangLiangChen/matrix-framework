package wang.liangchen.matrix.framework.ddd.domain.identity;

/**
 * @author Liangchen.Wang 2022-11-27 22:12
 * Marker interface
 * Mark a Simple Identity field of an Entity
 */
public interface ISimpleIdentity<T> extends IIdentity {
    T value();
}
