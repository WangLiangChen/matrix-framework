package wang.liangchen.matrix.framework.data.enumeration;

import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;

/**
 * @author Liangchen.Wang 2022-07-08 22:55
 */
public class StateEnum extends ConstantEnum {
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
    public final static StateEnum ACTIVE = new StateEnum("ACTIVE", "激活");
    public final static StateEnum INACTIVE = new StateEnum("INACTIVE", "无效");
    public final static StateEnum DRAFT = new StateEnum("DRAFT", "草稿");
    public final static StateEnum ENABLE = new StateEnum("ENABLE", "启用");
    public final static StateEnum DISABLE = new StateEnum("DISABLE", "禁用");


    public StateEnum(String name, String value) {
        super(name, value);
    }
}
