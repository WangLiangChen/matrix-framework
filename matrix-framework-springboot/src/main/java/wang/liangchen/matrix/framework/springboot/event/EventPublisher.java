package wang.liangchen.matrix.framework.springboot.event;

import org.springframework.context.ApplicationEvent;
import wang.liangchen.matrix.framework.springboot.context.BeanLoader;

/**
 * @author LiangChen.Wang
 */
public enum EventPublisher {
    /**
     * instance
     */
    INSTANCE;
    public void publishEvent(ApplicationEvent event) {
        BeanLoader.INSTANCE.getApplicationContext().publishEvent(event);
    }
}
