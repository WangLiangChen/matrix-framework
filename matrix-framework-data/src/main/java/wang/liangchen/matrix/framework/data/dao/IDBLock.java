package wang.liangchen.matrix.framework.data.dao;


import wang.liangchen.matrix.framework.springboot.api.ILock;

/**
 * @author LiangChen.Wang
 */
public interface IDBLock extends ILock {
    String TABLE_NAME = "matrix_lock";
}
