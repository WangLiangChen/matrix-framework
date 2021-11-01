package wang.liangchen.matrix.framework.commons.enumeration;

/**
 * @author wangliangchen
 * 参见 java.lang.reflect.Modifier
 */
public enum ModifierEnum {
    /**
     *
     */
    PUBLIC(0x0000_0001),
    PRIVATE(0x0000_0002),
    PROTECTED(0x0000_0004),
    STATIC(0x0000_0008),

    FINAL(0x0000_0010),
    SYNCHRONIZED(0x0000_0020),
    VOLATILE(0x0000_0040),
    TRANSIENT(0x0000_0080),

    NATIVE(0x0000_0100),
    INTERFACE(0x0000_0200),
    ABSTRACT(0x0000_0400),
    STRICT(0x0000_0800);

    // 通过移位运算的值
    private final int shiftValue;
    // 直接设置的16进制值
    private final int finalValue;

    ModifierEnum(int finalValue) {
        this.finalValue = finalValue;
        this.shiftValue = 1 << this.ordinal();
    }

    public int getShiftValue() {
        return shiftValue;
    }

    public int getFinalValue() {
        return finalValue;
    }
}
