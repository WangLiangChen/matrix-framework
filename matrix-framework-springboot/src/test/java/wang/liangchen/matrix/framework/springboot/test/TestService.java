package wang.liangchen.matrix.framework.springboot.test;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executor;

@Component
public class TestService {

    @Inject
    private Map<String,Executor> executorMap;
    @Async
    public void run() {
        System.out.println("run-----"+Thread.currentThread().getName());
    }
    @Scheduled(fixedRate = 300)
    public void fixedRateJob() {
        System.out.println("fixedRate 每隔3秒" + new Date()+Thread.currentThread().getName());
    }
}
