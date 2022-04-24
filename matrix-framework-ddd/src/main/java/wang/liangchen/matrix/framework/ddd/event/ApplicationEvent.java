package wang.liangchen.matrix.framework.ddd.event;

import java.time.Clock;

/**
 * @author Liangchen.Wang 2022-04-24 14:29
 * 应用事件
 */
public class ApplicationEvent extends DomainEvent {

    public ApplicationEvent(Object source) {
        super(source);
    }

    public ApplicationEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
