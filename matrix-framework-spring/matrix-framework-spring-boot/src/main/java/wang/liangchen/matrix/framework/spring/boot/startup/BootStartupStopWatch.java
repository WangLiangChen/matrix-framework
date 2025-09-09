package wang.liangchen.matrix.framework.spring.boot.startup;

import wang.liangchen.matrix.framework.commons.utils.StopWatch;

class BootStartupStopWatch {
    protected final static StopWatch stopWatch = new StopWatch();
    protected final static StopWatch.WatchTask watchTask = stopWatch.startTask("Startup");
}
