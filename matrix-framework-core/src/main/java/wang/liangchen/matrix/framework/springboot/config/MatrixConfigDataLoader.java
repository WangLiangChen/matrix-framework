package wang.liangchen.matrix.framework.springboot.config;

import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-06-21 11:57
 */
public class MatrixConfigDataLoader implements ConfigDataLoader<MatrixConfigDataSource> {
    private static final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static final PropertiesPropertySourceLoader propertiesPropertySourceLoader = new PropertiesPropertySourceLoader();
    private static final YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();

    @Override
    public ConfigData load(ConfigDataLoaderContext context, MatrixConfigDataSource matrixResource) throws IOException, ConfigDataResourceNotFoundException {
        String configRoot = matrixResource.getConfigRoot();
        List<PropertySource<?>> propertySources = new ArrayList<>();
        Resource[] jdbcResources = resourcePatternResolver.getResources(configRoot.concat(ConfigContext.JDBC_PATTERN));
        propertySources.addAll(resolveResources(jdbcResources, ConfigContext.JDBC_PREFIX));
        Resource[] loggerResources = resourcePatternResolver.getResources(configRoot.concat(ConfigContext.LOGGER_PATTERN));
        propertySources.addAll(resolveResources(loggerResources, ConfigContext.LOGGER_PREFIX));
        Resource[] autoscanResources = resourcePatternResolver.getResources(configRoot.concat(ConfigContext.AUTOSCAN_PATTERN));
        propertySources.addAll(resolveResources(autoscanResources, ConfigContext.AUTOSCAN_PREFIX));
        return new ConfigData(propertySources);
    }

    private List<PropertySource<?>> resolveResources(Resource[] resources, String propertySourceName) throws IOException {
        if (resources.length == 0) {
            return Collections.emptyList();
        }
        for (Resource resource : resources) {
            String configRoot = resource.getURI().toString();
            configRoot = configRoot.substring(0, configRoot.lastIndexOf('/'));
            // 初始化和比较是否目录一致
            boolean success = ConfigContext.INSTANCE.setConfigRoot(configRoot);
            if (!success) {
                continue;
            }
            String filename = resource.getFilename();
            if (filename.endsWith("yaml") || filename.endsWith("yml")) {
                return yamlPropertySourceLoader.load(propertySourceName, resource);
            }
            return propertiesPropertySourceLoader.load(propertySourceName, resource);
        }
        return Collections.emptyList();
    }
}
