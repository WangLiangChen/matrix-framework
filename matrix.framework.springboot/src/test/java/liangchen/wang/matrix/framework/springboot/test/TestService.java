package liangchen.wang.matrix.framework.springboot.test;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TestService {
    @Async
    public void run() {
        System.out.println("run----"+Thread.currentThread().getName());
    }
}
