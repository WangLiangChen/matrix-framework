package wang.liangchen.matrix.framework.commons.currency;


import wang.liangchen.matrix.framework.commons.exception.MessageLevel;
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
        ValidationUtil.INSTANCE.notNull(MessageLevel.WARN, fen, "fen can not by null");
        if (0 == fen) {
            return "0";
        }
        BigDecimal bigDecimal = NumberUtil.INSTANCE.divide(String.valueOf(fen), "100", 2);
        return bigDecimal.toString();
    }

    public String fen2Yuan(Integer fen) {
        ValidationUtil.INSTANCE.notNull(MessageLevel.WARN, fen, "fen can not by null");
        if (0 == fen) {
            return "0";
        }
        return fen2Yuan(fen.longValue());
    }

    public Long yuan2Fen(String yuan) {
        ValidationUtil.INSTANCE.notBlank(MessageLevel.WARN, yuan, "{ParameterNotBlank}");
        BigDecimal bigDecimal = NumberUtil.INSTANCE.multiply(yuan, "100", 0);
        return bigDecimal.longValue();
    }
}
