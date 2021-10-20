package liangchen.wang.matrix.framework.data.query;

import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;
import liangchen.wang.matrix.framework.commons.object.ObjectUtil;

/**
 * @author LiangChen.Wang 2019/11/13 17:51
 */
public class Between implements Cloneable {
    private static final Between self = new Between();

    public static Between newInstance(Object min, Object max) {
        try {
            Between between = ObjectUtil.INSTANCE.cast(self.clone());
            between.min = min;
            between.max = max;
            return between;
        } catch (CloneNotSupportedException e) {
            throw new MatrixErrorException(e);
        }
    }

    private Object min;
    private Object max;

    public Object getMin() {
        return min;
    }

    public Object getMax() {
        return max;
    }

}