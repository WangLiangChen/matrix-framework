package wang.liangchen.matrix.framework.data.util;


import wang.liangchen.matrix.framework.data.dao.ISequenceDao;
import wang.liangchen.matrix.framework.springboot.context.BeanLoader;

/**
 * @author LiangChen.Wang
 */
public enum SequenceUtil {
    /**
     *
     */
    INSTANCE;
    private final ISequenceDao sequenceDao = BeanLoader.INSTANCE.getBean("Matrix_Data_SequenceDao");

    public Long sequenceNumber(String sequenceKey) {
        return sequenceNumber(sequenceKey, 0L);
    }

    public Long sequenceNumber(String sequenceKey, long initialValue) {
        return sequenceDao.sequenceNumber(sequenceKey, initialValue);
    }
}
