package wang.liangchen.matrix.framework.springboot.env;

import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;

import java.io.IOException;

/**
 * @author Liangchen.Wang 2022-06-21 11:57
 */
public class MatrixConfigDataLoader implements ConfigDataLoader<MatrixConfigDataSource> {

    @Override
    public ConfigData load(ConfigDataLoaderContext context, MatrixConfigDataSource matrixResource) throws IOException, ConfigDataResourceNotFoundException {
        return new ConfigData(EnvironmentContext.INSTANCE.loadPropertySources(matrixResource));
    }


}
