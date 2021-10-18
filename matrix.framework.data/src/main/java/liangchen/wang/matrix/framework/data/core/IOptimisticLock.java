package liangchen.wang.matrix.framework.data.core;

/**
 * @author LiangChen.Wang
 */
public interface IOptimisticLock {

    Long getVersion();

    void setVersion(Long version);
}
