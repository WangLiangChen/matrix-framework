package liangchen.wang.matrix.framework.data.enumeration;

/**
 * @author LiangChen.Wang
 */
public enum OperationEnum {
    /**
     *
     */
    NONE("无效"), CREATE("创建"), RETRIEVE("读取"),
    UPDATE("更新"), DELETE("删除"), UPDATESTATUS("更新状态"), SUSPEND("停用"), RESUME("启用");

    private final String text;

    OperationEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
