package wang.liangchen.matrix.framework.springboot.event;

import wang.liangchen.matrix.framework.springboot.context.BeanLoader;
import org.springframework.context.ApplicationEvent;

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
