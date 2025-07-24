package wang.liangchen.matrix.framework.spring.web.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Author: Liangchen.Wang
 */
public class WebSocketMessageEvent extends ApplicationEvent {
    private String payload;

    public WebSocketMessageEvent(Object source, String payload) {
        super(source);
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
