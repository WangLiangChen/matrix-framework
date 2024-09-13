package wang.liangchen.matrix.framework.springboot.config;

import org.springframework.boot.context.config.ConfigDataResource;

import java.util.List;

/**
 * @author Liangchen.Wang 2022-06-21 11:55
 */
public class MatrixConfigDataSource extends ConfigDataResource {
    private final String configRoot;
    private final List<String> activeProfiles;

    public MatrixConfigDataSource(String configRoot, List<String> activeProfiles) {
        this.configRoot = configRoot;
        this.activeProfiles = activeProfiles;
    }

    public String getConfigRoot() {
        return configRoot;
    }

    public List<String> getActiveProfiles() {
        return activeProfiles;
    }
}
