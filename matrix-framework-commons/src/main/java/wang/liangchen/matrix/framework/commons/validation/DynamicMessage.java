package wang.liangchen.matrix.framework.commons.validation;

import wang.liangchen.matrix.framework.commons.string.StringUtil;

/**
 * @author Liangchen.Wang 2022-10-13 9:35
 */
class DynamicMessage {
    @DynamicMessageMaker
    private final String message;

    private DynamicMessage(String message) {
        this.message = message;
    }

    public static DynamicMessage newInstantce(String message) {
        return new DynamicMessage(message);
    }

    public static DynamicMessage newInstantce(String message, Object... args) {
        return new DynamicMessage(StringUtil.INSTANCE.format(message, args));
    }
}
