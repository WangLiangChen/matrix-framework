package wang.liangchen.matrix.framework.data.dao;

/**
 * @author LiangChen.Wang
 */
public interface IOptimisticLock {

    Long getVersion();

    void setVersion(Long version);
}