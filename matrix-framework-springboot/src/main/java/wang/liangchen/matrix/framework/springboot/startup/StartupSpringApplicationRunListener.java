package wang.liangchen.matrix.framework.springboot.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.Set;

public class StartupSpringApplicationRunListener implements SpringApplicationRunListener, Ordered {
    public StartupSpringApplicationRunListener(SpringApplication springApplication, String[] args) {
        if (null == springApplication) {
            return;
        }
        Set<Object> allSources = springApplication.getAllSources();
        allSources.forEach(e -> System.out.println("-" + e.getClass().getName()));
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}
