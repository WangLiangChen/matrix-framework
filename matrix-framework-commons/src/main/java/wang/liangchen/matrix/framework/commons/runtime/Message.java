package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.string.StringUtil;

/**
 * @author Liangchen.Wang
 */
public class Message {
    private String code = "0";
    private final String message;

    protected Message(String message, Object... args) {
        this.message = StringUtil.INSTANCE.format(message, args);
    }

    public static Message of(String message, Object... args) {
        return new Message(message, args);
    }

    public Message withCode(String code) {
        this.code = code;
        return this;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
