package wang.liangchen.matrix.framework.spring.boot.config;

import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.core.env.PropertySource;
import wang.liangchen.matrix.framework.spring.boot.context.EnvironmentContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Liangchen.Wang 2022-06-21 11:57
 */
public class MatrixConfigDataLoader implements ConfigDataLoader<MatrixConfigDataSource> {

    /**
     * 没有配置 spring.config.import=matrix://xxx. 会调用1次，参数为classpath*:
     * 配置了  spring.config.import=matrix://xxx. 会调用2次，参数分别为classpath*:,xxx
     */
    @Override
    public ConfigData load(ConfigDataLoaderContext configDataLoaderContext, MatrixConfigDataSource matrixConfigDataSource) throws ConfigDataResourceNotFoundException {
        return new ConfigData(new ArrayList<>());
    }
}
