package liangchen.wang.matrix.framework.springboot.runner;

import liangchen.wang.matrix.framework.commons.utils.PrettyPrinter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author LiangChen.Wang
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class SystemStartedRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        PrettyPrinter.INSTANCE.buffer("System Started......");
        PrettyPrinter.INSTANCE.flush();
    }
}
