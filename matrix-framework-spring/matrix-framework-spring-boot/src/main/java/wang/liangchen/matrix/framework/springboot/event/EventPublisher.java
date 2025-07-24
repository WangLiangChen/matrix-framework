package wang.liangchen.matrix.framework.springboot.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

public enum EventPublisher {
    INSTANCE;
    private ApplicationContext applicationContext;

    public void resetApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void publishEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }
}
