package wang.liangchen.matrix.framework.commons.utils;

import wang.liangchen.matrix.framework.commons.exception.AssertUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author WangLiangChen
 */
public enum BigDecimalUtil {
    /**
     * instance
     */
    INSTANCE;

    public BigDecimal add(String v1, String v2, int scale) {
        AssertUtil.INSTANCE.notBlank(v1, "v1 can not be blank");
        AssertUtil.INSTANCE.notBlank(v2, "v1 can not be blank");
        AssertUtil.INSTANCE.isTrue(scale > -1, "scale must greater than -1");
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return add(b1, b2, scale);
    }

    public BigDecimal add(BigDecimal v1, BigDecimal v2, int scale) {
        AssertUtil.INSTANCE.notNull(v1, "v1 can not be null");
        AssertUtil.INSTANCE.notNull(v2, "v1 can not be null");
        AssertUtil.INSTANCE.isTrue(scale > -1, "scale must greater than -1");
        return calculate(Operator.ADD, v1, v2, scale);
    }

    public BigDecimal subtract(String v1, String v2, int scale) {
        AssertUtil.INSTANCE.notBlank(v1, "v1 can not be blank");
        AssertUtil.INSTANCE.notBlank(v2, "v1 can not be blank");
        AssertUtil.INSTANCE.isTrue(scale > -1, "scale must greater than -1");
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return subtract(b1, b2, scale);
    }

    public BigDecimal subtract(BigDecimal v1, BigDecimal v2, int scale) {
        AssertUtil.INSTANCE.notNull(v1, "v1 can not be null");
        AssertUtil.INSTANCE.notNull(v2, "v1 can not be null");
        AssertUtil.INSTANCE.isTrue(scale > -1, "scale must greater than -1");
        return calculate(Operator.SUBTRACT, v1, v2, scale);
    }

    public BigDecimal multiply(String v1, String v2, int scale) {
        AssertUtil.INSTANCE.notBlank(v1, "v1 can not be blank");
        AssertUtil.INSTANCE.notBlank(v2, "v1 can not be blank");
        AssertUtil.INSTANCE.isTrue(scale > -1, "scale must greater than -1");
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return multiply(b1, b2, scale);
    }

    public BigDecimal multiply(BigDecimal v1, BigDecimal v2, int scale) {
        AssertUtil.INSTANCE.notNull(v1, "v1 can not be null");
        AssertUtil.INSTANCE.notNull(v2, "v1 can not be null");
        AssertUtil.INSTANCE.isTrue(scale > -1, "scale must greater than -1");
        return calculate(Operator.MULTIPLY, v1, v2, scale);
    }

    public BigDecimal divide(String v1, String v2, int scale) {
        AssertUtil.INSTANCE.notBlank(v1, "v1 can not be blank");
        AssertUtil.INSTANCE.notBlank(v2, "v1 can not be blank");
        AssertUtil.INSTANCE.isTrue(scale > -1, "scale must greater than -1");
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return divide(b1, b2, scale);
    }

    public BigDecimal divide(BigDecimal v1, BigDecimal v2, int scale) {
        AssertUtil.INSTANCE.notNull(v1, "v1 can not be null");
        AssertUtil.INSTANCE.notNull(v2, "v1 can not be null");
        AssertUtil.INSTANCE.isTrue(scale > -1, "scale must greater than -1");
        return calculate(Operator.DIVIDE, v1, v2, scale);
    }


    public BigDecimal round(BigDecimal v) {
        AssertUtil.INSTANCE.notNull(v, "v can not be null");
        return v.divide(new BigDecimal(1)).setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal calculate(Operator operator, BigDecimal v, int scale) {
        switch (operator) {
            case ABS:
                return v.setScale(scale, RoundingMode.HALF_UP).abs();
            default:
                return new BigDecimal(0);
        }
    }

    private BigDecimal calculate(Operator operator, BigDecimal v1, BigDecimal v2, int scale) {
        switch (operator) {
            case ADD:
                return v1.add(v2).setScale(scale, RoundingMode.HALF_UP);
            case SUBTRACT:
                return v1.subtract(v2).setScale(scale, RoundingMode.HALF_UP);
            case MULTIPLY:
                return v1.multiply(v2).setScale(scale, RoundingMode.HALF_UP);
            case DIVIDE:
                return v1.divide(v2).setScale(scale, RoundingMode.HALF_UP);
            default:
                return new BigDecimal(0);
        }
    }

    private enum Operator {
        /**
         * 运算符
         */
        ADD, SUBTRACT, MULTIPLY, DIVIDE, ABS
    }

}
