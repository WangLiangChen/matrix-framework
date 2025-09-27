package wang.liangchen.matrix.framework.commons.currency;


import wang.liangchen.matrix.framework.commons.number.NumberUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

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
        ValidationUtil.INSTANCE.notNull(fen, "fen must not be null");
        if (0 == fen) {
            return "0";
        }
        BigDecimal bigDecimal = NumberUtil.INSTANCE.divide(String.valueOf(fen), "100", 2);
        return bigDecimal.toString();
    }

    public String fen2Yuan(Integer fen) {
        ValidationUtil.INSTANCE.notNull(fen, "fen must not be null");
        if (0 == fen) {
            return "0";
        }
        return fen2Yuan(fen.longValue());
    }

    public Long yuan2Fen(String yuan) {
        ValidationUtil.INSTANCE.notNullAndBlank(yuan, "yuan must not be null or blank");
        BigDecimal bigDecimal = NumberUtil.INSTANCE.multiply(yuan, "100", 0);
        return bigDecimal.longValue();
    }
}
