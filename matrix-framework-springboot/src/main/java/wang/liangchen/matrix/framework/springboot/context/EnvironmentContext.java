package wang.liangchen.matrix.framework.springboot.context;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

public enum EnvironmentContext {
    INSTANCE;
    private Environment environment;

    public void setEnvironment(ConfigurableEnvironment environment) {

    }
}
