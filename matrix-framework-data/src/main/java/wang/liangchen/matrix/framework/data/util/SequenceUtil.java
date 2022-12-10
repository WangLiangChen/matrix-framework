package wang.liangchen.matrix.framework.data.util;


import wang.liangchen.matrix.framework.data.dao.ISequenceDao;
import wang.liangchen.matrix.framework.data.dao.SequenceKey;
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

    public Long sequenceNumber(String sequenceGroup, String sequenceKey) {
        return sequenceNumber(sequenceGroup, sequenceKey, 0L);
    }

    public Long sequenceNumber(String sequenceGroup, String sequenceKey, long initialValue) {
        return sequenceNumber(SequenceKey.newSequence(sequenceGroup, sequenceKey, initialValue));
    }

    public Long sequenceNumber(SequenceKey sequenceKey) {
        return sequenceDao.sequenceNumber(sequenceKey);
    }
}
