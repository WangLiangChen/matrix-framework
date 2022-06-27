package wang.liangchen.matrix.framework.springboot.env;

import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-06-25 16:07
 */
public enum EnvironmentContext {
    INSTANCE;
    public static final String JDBC_PREFIX = "jdbc";
    public static final String LOGGER_PREFIX = "logger";
    public static final String AUTOSCAN_PREFIX = "autoscan";
    private static final String YAML = "yaml";
    private static final String YML = "yml";
    private static final String CONFIG_DIRECTORY = "/matrix-framework";
    private final static String CONFIG_ROOT = "configRoot";
    private static final String EXTENSION_PATTERN = ".*";
    private static final String JDBC_PATTERN = Symbol.URI_SEPARATOR.getSymbol() + JDBC_PREFIX + EXTENSION_PATTERN;
    private static final String LOGGER_PATTERN = Symbol.URI_SEPARATOR.getSymbol() + LOGGER_PREFIX + EXTENSION_PATTERN;
    private static final String AUTOSCAN_PATTERN = Symbol.URI_SEPARATOR.getSymbol() + AUTOSCAN_PREFIX + EXTENSION_PATTERN;

    private static final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static final PropertiesPropertySourceLoader propertiesPropertySourceLoader = new PropertiesPropertySourceLoader();
    private static final YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();

    private String configRoot;
    private URI configRootURI;
    private Environment environment;
    private List<String> activeProfiles;

    void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getConfigRoot() {
        if (null == configRoot) {
            throw new MatrixErrorException("configRoot has not been initialized");
        }
        return configRoot;
    }

    public URI getConfigRootURI() {
        if (null == configRootURI) {
            throw new MatrixErrorException("configRoot has not been initialized");
        }
        return configRootURI;
    }


    public String getLocation(String relativePath) {
        return this.configRoot.concat(relativePath);
    }

    public URI getURI(String relativePath) {
        return this.configRootURI.resolve(relativePath);
    }

    private String resolveConfigRoot() {
        String configRoot = System.getenv(CONFIG_ROOT);
        if (StringUtil.INSTANCE.isNotBlank(configRoot)) {
            PrettyPrinter.INSTANCE.buffer("'configRoot' is found in 'System.getenv' : {}", configRoot);
            return configRoot;
        }
        PrettyPrinter.INSTANCE.buffer("'configRoot' isn't found in 'System.getenv'");

        configRoot = System.getProperty(CONFIG_ROOT);
        if (StringUtil.INSTANCE.isNotBlank(configRoot)) {
            PrettyPrinter.INSTANCE.buffer("'configRoot' is found in 'System.getProperty' : {}", configRoot);
            return configRoot;
        }
        PrettyPrinter.INSTANCE.buffer("'configRoot' isn't found in 'System.getProperty'");

        /*
        Resource resource = new ClassPathResource(Symbol.BLANK.getSymbol());
        try {
            configRoot = resource.getURI().toString();
        } catch (IOException e) {
            PrettyPrinter.INSTANCE.flush();
            throw new MatrixErrorException("An error occurred:" + e.getMessage());
        }
        PrettyPrinter.INSTANCE.buffer("'configRoot' is found in classpath': {}", configRoot);
        */
        return null;
    }

    List<PropertySource<?>> loadPropertySources(MatrixConfigDataSource matrixConfigDataSource) {
        // 已经加载过
        if (null != this.configRoot) {
            PrettyPrinter.INSTANCE.buffer("configRoot is loaded:{}", this.configRoot);
            return Collections.emptyList();
        }
        this.activeProfiles = matrixConfigDataSource.getActiveProfiles();
        PrettyPrinter.INSTANCE.buffer("activeProfiles is: {}", activeProfiles);
        String profile = this.activeProfiles.isEmpty() ? CONFIG_DIRECTORY
                : CONFIG_DIRECTORY.concat(Symbol.HYPHEN.getSymbol()).concat(this.activeProfiles.get(0));
        PrettyPrinter.INSTANCE.buffer("actived config directory: {}", profile);
        String resolvedConfigRoot = resolveConfigRoot();
        if (null != resolvedConfigRoot) {
            return resolvePropertySources(resolvedConfigRoot, profile, true);
        }

        List<PropertySource<?>> propertySources = resolvePropertySources(matrixConfigDataSource.getConfigRoot(), profile, false);
        PrettyPrinter.INSTANCE.flush();
        return propertySources;
    }

    private List<PropertySource<?>> resolvePropertySources(String configRoot, String profile, boolean mandatory) {
        // 构造pattern
        configRoot = configRoot.concat(profile);
        List<PropertySource<?>> propertySources = new ArrayList<>();
        try {
            Resource[] jdbcResources = resourcePatternResolver.getResources(configRoot.concat(EnvironmentContext.JDBC_PATTERN));
            Resource[] loggerResources = resourcePatternResolver.getResources(configRoot.concat(EnvironmentContext.LOGGER_PATTERN));
            Resource[] autoscanResources = resourcePatternResolver.getResources(configRoot.concat(EnvironmentContext.AUTOSCAN_PATTERN));

            populateConfigRoot(configRoot, mandatory, jdbcResources, loggerResources, autoscanResources);

            propertySources.addAll(resolvePropertySources(jdbcResources, JDBC_PREFIX));
            propertySources.addAll(resolvePropertySources(loggerResources, LOGGER_PREFIX));
            propertySources.addAll(resolvePropertySources(autoscanResources, AUTOSCAN_PREFIX));
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
        return propertySources;
    }

    private void populateConfigRoot(String configRoot, boolean mandatory, Resource[]... allResources) throws IOException {
        if (mandatory) {
            this.configRoot = configRoot;
            this.configRootURI = URI.create(this.configRoot);
            PrettyPrinter.INSTANCE.buffer("resolved configRoot is :{}", configRoot);
            return;
        }
        for (Resource[] resources : allResources) {
            for (Resource resource : resources) {
                String uri = resource.getURI().toString();
                this.configRoot = uri.substring(0, uri.lastIndexOf('/'));
                this.configRootURI = URI.create(this.configRoot);
                PrettyPrinter.INSTANCE.buffer("configRoot from 'spring.config.import' is :{}", this.configRoot);
                return;
            }
        }
    }

    private List<PropertySource<?>> resolvePropertySources(Resource[] resources, String propertySourceName) throws IOException {
        if (resources.length == 0) {
            return Collections.emptyList();
        }
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename.endsWith(YAML) || filename.endsWith(YML)) {
                return yamlPropertySourceLoader.load(propertySourceName, resource);
            }
            return propertiesPropertySourceLoader.load(propertySourceName, resource);
        }
        return Collections.emptyList();
    }
}
