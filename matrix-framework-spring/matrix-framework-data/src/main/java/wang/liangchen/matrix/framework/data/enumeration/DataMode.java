package wang.liangchen.matrix.framework.data.enumeration;

import java.util.Objects;

/**
 * @author LiangChen.Wang
 */
public enum DataMode {
    //数据权限模式：读取(4)修改(2)删除(1)没有权限(0) ,curd
    A((byte) -1, "所有操作"),
    R((byte) 4, "读取"),
    U((byte) 2, "更新"),
    D((byte) 1, "删除"),
    N((byte) 0, "无操作"),
    RUD((byte) 7, "读取/更新/删除"),
    RD((byte) 5, "读取/删除"),
    UD((byte) 3, "更新/删除"),
    RU((byte) 6, "读取/更新");
    private final Byte value;
    private final String text;

    DataMode(Byte privilege_value, String text) {
        this.value = privilege_value;
        this.text = text;
    }

    public Byte getValue() {
        return this.value;
    }

    public String getText() {
        return this.text;
    }

    public boolean check(int value) {
        return (this.value & value) == value;
    }

    public boolean check(DataMode dataMode) {
        return check(dataMode.getValue());
    }

    public static DataMode valueOf(Byte value) {
        DataMode[] enumConstants = DataMode.class.getEnumConstants();
        for (DataMode dataMode : enumConstants) {
            if (Objects.equals(dataMode.getValue(), value)) {
                return dataMode;
            }
        }
        return DataMode.N;
        //将来扩展根据不同的分组
//		 Integer one = data_mode%10;
//		 Integer ten = data_mode/10%10;
//		 Integer hundred = data_mode/100%10;
//		 Integer thousand = data_mode/1000%10;
    }
}
