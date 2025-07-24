package wang.liangchen.matrix.framework.spring.data.enumeration;

/**
 * @author LiangChen.Wang
 */
public enum DataMode {
    C(1, "CREATE"),
    D(1 << 1, "DELETE"),
    U(1 << 2, "UPDATE"),
    R(1 << 3, "RETRIEVE"),
    A(-1, "ALL"),
    N(0, "NONE");

    private final Integer value;
    private final String text;

    DataMode(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public boolean checkValue(int privilegeValue) {
        return (value & privilegeValue) == value;
    }

    public static int privilegeValue(DataMode... dataModes) {
        int value = 0;
        for (DataMode dataMode : dataModes) {
            value |= dataMode.getValue();
        }
        return value;
    }

}
