package wang.liangchen.matrix.framework.data.logging.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

/**
 * @author Liangchen.Wang 2022-09-20 8:34
 */
public class RdbmsAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Override
    public void start() {
        System.out.println("=====> start");
        super.start();
    }

    @Override
    public void stop() {
        System.out.println("=====> stop");
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (event == null || !isStarted()) {
            return;
        }
    }

}
