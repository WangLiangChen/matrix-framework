package wang.liangchen.matrix.framework.web.sse;

import org.springframework.context.ApplicationEvent;

/**
 * @author LiangChen.Wang
 */
public class SseEvent extends ApplicationEvent {
    private final String message;

    public SseEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
