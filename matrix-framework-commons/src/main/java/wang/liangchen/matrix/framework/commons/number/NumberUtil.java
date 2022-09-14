package wang.liangchen.matrix.framework.commons.number;

import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Liangchen.Wang 2022-04-02 9:08
 */
public enum NumberUtil {
    INSTANCE;

    public byte decimalToByte(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.byteValue();
        }
        return decimal.byteValueExact();
    }

    public short decimalToShort(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.shortValue();
        }
        return decimal.shortValueExact();
    }

    public int decimalToInt(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.intValue();
        }
        return decimal.intValueExact();
    }

    public long decimalToLong(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.longValue();
        }
        return decimal.longValueExact();
    }

    public float decimalToFloat(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        return decimal.floatValue();
    }

    public double decimalToDouble(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        return decimal.floatValue();
    }

    public BigDecimal add(String v1, String v2, int scale) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,v1, "v1 must not be blank");
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,v2, "v1 must not be blank");
        ValidationUtil.INSTANCE.isTrue(ExceptionLevel.WARN,scale > -1, "scale must greater than -1");
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return add(b1, b2, scale);
    }

    public BigDecimal add(BigDecimal v1, BigDecimal v2, int scale) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN,v1, "v1 must not be null");
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN,v2, "v1 must not be null");
        ValidationUtil.INSTANCE.isTrue(ExceptionLevel.WARN,scale > -1, "scale must greater than -1");
        return decimalCalculate(NumberUtil.Operator.ADD, v1, v2, scale);
    }

    public BigDecimal subtract(String v1, String v2, int scale) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,v1, "v1 must not be blank");
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,v2, "v1 must not be blank");
        ValidationUtil.INSTANCE.isTrue(ExceptionLevel.WARN,scale > -1, "scale must greater than -1");
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return subtract(b1, b2, scale);
    }

    public BigDecimal subtract(BigDecimal v1, BigDecimal v2, int scale) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN,v1, "v1 must not be null");
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN,v2, "v1 must not be null");
        ValidationUtil.INSTANCE.isTrue(ExceptionLevel.WARN,scale > -1, "scale must greater than -1");
        return decimalCalculate(NumberUtil.Operator.SUBTRACT, v1, v2, scale);
    }

    public BigDecimal multiply(String v1, String v2, int scale) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,v1, "v1 must not be blank");
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,v2, "v1 must not be blank");
        ValidationUtil.INSTANCE.isTrue(ExceptionLevel.WARN,scale > -1, "scale must greater than -1");
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return multiply(b1, b2, scale);
    }

    public BigDecimal multiply(BigDecimal v1, BigDecimal v2, int scale) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN,v1, "v1 must not be null");
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN,v2, "v1 must not be null");
        ValidationUtil.INSTANCE.isTrue(ExceptionLevel.WARN,scale > -1, "scale must greater than -1");
        return decimalCalculate(NumberUtil.Operator.MULTIPLY, v1, v2, scale);
    }

    public BigDecimal divide(String v1, String v2, int scale) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,v1, "v1 must not be blank");
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,v2, "v1 must not be blank");
        ValidationUtil.INSTANCE.isTrue(ExceptionLevel.WARN,scale > -1, "scale must greater than -1");
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return divide(b1, b2, scale);
    }

    public BigDecimal divide(BigDecimal v1, BigDecimal v2, int scale) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN,v1, "v1 must not be null");
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN,v2, "v1 must not be null");
        ValidationUtil.INSTANCE.isTrue(ExceptionLevel.WARN,scale > -1, "scale must greater than -1");
        return decimalCalculate(NumberUtil.Operator.DIVIDE, v1, v2, scale);
    }


    private BigDecimal decimalCalculate(NumberUtil.Operator operator, BigDecimal v1, BigDecimal v2, int scale) {
        return decimalCalculate(operator,v1,v2,scale,RoundingMode.HALF_UP);
    }
    private BigDecimal decimalCalculate(NumberUtil.Operator operator, BigDecimal v1, BigDecimal v2, int scale, RoundingMode roundingMode) {
        switch (operator) {
            case ADD:
                return v1.add(v2).setScale(scale, roundingMode);
            case SUBTRACT:
                return v1.subtract(v2).setScale(scale, roundingMode);
            case MULTIPLY:
                return v1.multiply(v2).setScale(scale, roundingMode);
            case DIVIDE:
                return v1.divide(v2).setScale(scale, roundingMode);
            default:
                return new BigDecimal(0);
        }
    }

    private enum Operator {
        /**
         * 运算符
         */
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }
}
