package wang.liangchen.matrix.framework.web.push;

import org.springframework.context.ApplicationEvent;

/**
 * @author LiangChen.Wang
 */
public class PushEvent extends ApplicationEvent {
    private final String message;

    public PushEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
