package liangchen.wang.matrix.framework.springboot.event;

import liangchen.wang.gradf.framework.springboot.context.BeanLoader;
import org.springframework.context.ApplicationContext;
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
