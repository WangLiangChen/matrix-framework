package liangchen.wang.matrix.framework.springboot.test;

import liangchen.wang.matrix.framework.commons.thread.ThreadUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;

@SpringBootTest
public class SpringbootTest {
    @Inject
    private TestService service;
    @Test
    public void testService(){
        service.run();
        ThreadUtil.INSTANCE.sleep(1000);
    }
}
