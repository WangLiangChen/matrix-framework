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
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

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
    public static final String JDBC_PREFIX = "jdbc";
    public static final String LOGGER_PREFIX = "logger";
    public static final String AUTOSCAN_PREFIX = "autoscan";
    private static final String EXTENSION_PATTERN = ".*";
    private static final String JDBC_PATTERN = Symbol.URI_SEPARATOR.getSymbol() + JDBC_PREFIX + EXTENSION_PATTERN;
    private static final String LOGGER_PATTERN = Symbol.URI_SEPARATOR.getSymbol() + LOGGER_PREFIX + EXTENSION_PATTERN;
    private static final String AUTOSCAN_PATTERN = Symbol.URI_SEPARATOR.getSymbol() + AUTOSCAN_PREFIX + EXTENSION_PATTERN;

    public static String configRoot;

    @Override
    public ConfigData load(ConfigDataLoaderContext context, MatrixConfigDataSource matrixResource) throws IOException, ConfigDataResourceNotFoundException {
        String configRoot = matrixResource.getConfigRoot();
        List<PropertySource<?>> propertySources = new ArrayList<>();
        Resource[] jdbcResources = resourcePatternResolver.getResources(configRoot.concat(JDBC_PATTERN));
        propertySources.addAll(resolveResources(jdbcResources, JDBC_PREFIX));
        Resource[] loggerResources = resourcePatternResolver.getResources(configRoot.concat(LOGGER_PATTERN));
        propertySources.addAll(resolveResources(loggerResources, LOGGER_PREFIX));
        Resource[] autoscanResources = resourcePatternResolver.getResources(configRoot.concat(AUTOSCAN_PATTERN));
        propertySources.addAll(resolveResources(autoscanResources, AUTOSCAN_PREFIX));
        return new ConfigData(propertySources);
    }

    private List<PropertySource<?>> resolveResources(Resource[] resources, String propertySourceName) throws IOException {
        if (resources.length == 0) {
            return Collections.emptyList();
        }
        for (Resource resource : resources) {
            String uriString = resource.getURI().toString();
            uriString = uriString.substring(0, uriString.lastIndexOf('/'));
            // 初始化首次命中的文件目录
            if (null == configRoot) {
                configRoot = uriString;
            }
            // 不是同一个目录 略过
            if (!uriString.equals(configRoot)) {
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
