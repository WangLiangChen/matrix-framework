package wang.liangchen.matrix.framework.data.annotation;

/**
 * @author Liangchen.Wang 2022-06-15 20:08
 */
public enum IdStrategy {
    AUTO_INCREMENT,
    UUID,
    NANOID,
    /**
     * 类似雪花算法，自动生成节点位
     */
    SEQUENCE_NODE
}