package liangchen.wang.matrix.framework.springboot.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author LiangChen.Wang
 */
@Component
public class StartedCommandLineRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        // Printer.INSTANCE.prettyPrint("System Started......");
    }
}
