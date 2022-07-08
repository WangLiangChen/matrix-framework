package wang.liangchen.matrix.framework.data.dao.entity;

import java.io.Serializable;

/**
 * @author Liangchen.Wang 2022-07-08 9:49
 */
public interface Identity<T> extends Serializable {
    T value();
}
