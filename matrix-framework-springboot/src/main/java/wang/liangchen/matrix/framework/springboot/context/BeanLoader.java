package wang.liangchen.matrix.framework.springboot.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public enum BeanLoader {
    INSTANCE;
    private ApplicationContext applicationContext;

    public void resetApplicationContext(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
