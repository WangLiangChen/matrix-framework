package wang.liangchen.matrix.framework.ddd.northbound_ohs.local;

import wang.liangchen.matrix.framework.ddd.domain.DomainEvent;

import java.time.Clock;

/**
 * @author Liangchen.Wang 2022-04-24 14:29
 * Marker interface
 * ApplicationEvent
 */
public class ApplicationEvent extends DomainEvent {

    public ApplicationEvent(Object source) {
        super(source);
    }

    public ApplicationEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
