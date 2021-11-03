package wang.liangchen.matrix.framework.commons.utils;


import wang.liangchen.matrix.framework.commons.exception.AssertUtil;

import java.math.BigDecimal;

/**
 * @author LiangChen.Wang 2020/9/14
 */
public enum RmbUtil {
    /**
     * instance
     */
    INSTANCE;

    public String fen2Yuan(Long fen) {
        AssertUtil.INSTANCE.notNull(fen, "fen can not by null");
        if (0 == fen) {
            return "0";
        }
        BigDecimal bigDecimal = BigDecimalUtil.INSTANCE.divide(String.valueOf(fen), "100", 2);
        return bigDecimal.toString();
    }

    public String fen2Yuan(Integer fen) {
        AssertUtil.INSTANCE.notNull(fen, "fen can not by null");
        if (0 == fen) {
            return "0";
        }
        return fen2Yuan(fen.longValue());
    }

    public Long yuan2Fen(String yuan) {
        AssertUtil.INSTANCE.notBlank(yuan, "yuan can not by null");
        BigDecimal bigDecimal = BigDecimalUtil.INSTANCE.multiply(yuan, "100", 0);
        return bigDecimal.longValue();
    }
}
