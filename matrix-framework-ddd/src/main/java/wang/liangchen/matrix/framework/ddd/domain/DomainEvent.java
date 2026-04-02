package wang.liangchen.matrix.framework.ddd.domain;

import java.time.Clock;
import java.util.EventObject;

/**
 * Represents a domain event.
 *
 * @author Liangchen.Wang
 * @since 2022-04-24
 */
public class DomainEvent extends EventObject {

    /**
     * The timestamp indicating when the event was created, in milliseconds since the epoch(1970-01-01T00:00:00Z).
     */
    private final long timestamp;

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
