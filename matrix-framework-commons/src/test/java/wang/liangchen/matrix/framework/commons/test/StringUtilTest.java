package wang.liangchen.matrix.framework.commons.test;

import wang.liangchen.matrix.framework.commons.utils.StopWatch;

import java.util.concurrent.TimeUnit;

public class StringUtilTest {
    public static void main(String[] args) throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.startTask("a").addMessage("hello");
        stopWatch.startTask("b");
        stopWatch.startTask("c");
        TimeUnit.SECONDS.sleep(2);
        stopWatch.stopTask("a");
        stopWatch.prettyPrint();

    }
}
