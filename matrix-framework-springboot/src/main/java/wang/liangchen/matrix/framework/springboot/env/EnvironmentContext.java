package wang.liangchen.matrix.framework.springboot.env;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import wang.liangchen.matrix.framework.springboot.config.MatrixConfigDataSource;

import java.util.Collection;
import java.util.Collections;

public enum EnvironmentContext {
    INSTANCE;
    private Environment environment;

    public void setEnvironment(ConfigurableEnvironment environment) {

    }

    /**
     * 该方法会被ConfigDataLoader调用
     * 没有配置 spring.config.import=matrix://xxx. 会调用1次，参数为classpath*:
     * 配置了  spring.config.import=matrix://xxx. 会调用2次，参数分别为classpath*:,xxx
     *
     * @param matrixConfigDataSource activeProfiles and configRoot
     * @return PropertySources
     */
    public Collection<? extends PropertySource<?>> loadPropertySources(MatrixConfigDataSource matrixConfigDataSource) {
        return Collections.emptyList();
    }
}
