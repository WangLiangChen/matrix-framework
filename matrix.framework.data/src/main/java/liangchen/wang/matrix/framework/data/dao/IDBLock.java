package liangchen.wang.matrix.framework.data.dao;


import liangchen.wang.matrix.framework.springboot.api.ILock;

/**
 * @author LiangChen.Wang
 */
public interface IDBLock extends ILock {
    String TABLE_NAME = "matrix_lock";
}
