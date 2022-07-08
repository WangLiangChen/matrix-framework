package wang.liangchen.matrix.framework.data.enumeration;

import wang.liangchen.matrix.framework.commons.enumeration.CommonEnum;

/**
 * @author Liangchen.Wang 2022-07-08 22:55
 */
public class StateEnum extends CommonEnum {
    public final static StateEnum NONE = new StateEnum("NONE", "无状态");
    public final static StateEnum NORMAL = new StateEnum("NORMAL", "正常");
    public final static StateEnum INITIAL = new StateEnum("INITIAL", "初始");
    public final static StateEnum PENDING = new StateEnum("PENDING", "待审核");
    public final static StateEnum REJECT = new StateEnum("REJECT", "驳回");
    public final static StateEnum SUSPEND = new StateEnum("SUSPEND", "暂停");
    public final static StateEnum DELETED = new StateEnum("DELETED", "删除");
    public final static StateEnum INPROGRESS = new StateEnum("INPROGRESS", "进行中");
    public final static StateEnum COMPLETED = new StateEnum("COMPLETED", "完成");
    public final static StateEnum FAILED = new StateEnum("FAILED", "失败");

    public StateEnum(String name, String value) {
        super(name, value);
    }

    public static String[] names(StateEnum[] stateEnums) {
        if (null == stateEnums) {
            return null;
        }
        String[] names = new String[stateEnums.length];
        for (int i = 0; i < stateEnums.length; i++) {
            names[i] = stateEnums[i].name();
        }
        return names;
    }
}
