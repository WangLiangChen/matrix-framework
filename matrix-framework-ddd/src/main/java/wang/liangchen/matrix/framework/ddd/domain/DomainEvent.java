package wang.liangchen.matrix.framework.ddd.domain;

import java.time.Clock;
import java.util.EventObject;

/**
 * @author Liangchen.Wang 2022-04-24 14:29
 * 领域事件
 */
public class DomainEvent extends EventObject {

    private final long timestamp;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DomainEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public DomainEvent(Object source, Clock clock) {
        super(source);
        this.timestamp = clock.millis();
    }

    public final long getTimestamp() {
        return this.timestamp;
    }
}
