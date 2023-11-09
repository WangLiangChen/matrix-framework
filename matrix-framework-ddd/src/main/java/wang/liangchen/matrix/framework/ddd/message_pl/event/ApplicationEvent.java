package wang.liangchen.matrix.framework.ddd.message_pl.event;

import java.time.Clock;
import java.util.EventObject;

/**
 * @author Liangchen.Wang 2022-04-24 14:29
 * ApplicationEvent
 */
public class ApplicationEvent extends EventObject {

    private final long timestamp;

    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public ApplicationEvent(Object source, Clock clock) {
        super(source);
        this.timestamp = clock.millis();
    }

    public final long getTimestamp() {
        return this.timestamp;
    }
}
