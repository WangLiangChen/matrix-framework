package liangchen.wang.matrix.framework.data.core;

/**
 * @author LiangChen.Wang
 */
public interface IOptimisticLockEntity {

    Long getVersion();

    void setVersion(Long version);
}
