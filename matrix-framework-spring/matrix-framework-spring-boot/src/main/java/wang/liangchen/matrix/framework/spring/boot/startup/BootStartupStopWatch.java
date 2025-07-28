package wang.liangchen.matrix.framework.spring.boot.startup;

import wang.liangchen.matrix.framework.commons.utils.StopWatch;

public class BootStartupStopWatch {
    public final static StopWatch stopWatch = new StopWatch();
    public final static StopWatch.WatchTask watchTask = stopWatch.startTask("Startup");
}
