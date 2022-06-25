package wang.liangchen.matrix.framework.springboot.config;

import org.springframework.boot.context.config.ConfigDataResource;

/**
 * @author Liangchen.Wang 2022-06-21 11:55
 */
public class MatrixConfigDataSource extends ConfigDataResource {
    private final String configRoot;

    public MatrixConfigDataSource(String configRoot) {
        this.configRoot = configRoot;
    }

    public String getConfigRoot() {
        return configRoot;
    }

}
