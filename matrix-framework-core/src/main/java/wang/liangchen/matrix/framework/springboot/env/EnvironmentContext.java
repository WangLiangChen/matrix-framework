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
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.network.URIUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
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
    private static final String CLASSPATH = "classpath";
    private static final String YAML = "yaml";
    private static final String YML = "yml";
    private static final String CONFIG_DIRECTORY = "matrix-framework";
    private final static String CONFIG_ROOT = "configRoot";
    private static final String EXTENSION_PATTERN = ".*";
    private static final String JDBC_PATTERN = JDBC_PREFIX + EXTENSION_PATTERN;
    private static final String LOGGER_PATTERN = LOGGER_PREFIX + EXTENSION_PATTERN;
    private static final String AUTOSCAN_PATTERN = AUTOSCAN_PREFIX + EXTENSION_PATTERN;

    private static final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static final PropertiesPropertySourceLoader propertiesPropertySourceLoader = new PropertiesPropertySourceLoader();
    private static final YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();

    private URI configRootURI;
    private URL configRootURL;
    private Environment environment;
    private List<String> activeProfiles;

    void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public URI getConfigRootURI() {
        if (null == configRootURI) {
            throw new MatrixErrorException("configRoot has not been initialized");
        }
        return configRootURI;
    }

    public URI getURI(String relativePath) {
        return this.configRootURI.resolve(relativePath);
    }

    private String populateConfigRoot() {
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
        return null;
    }

    List<PropertySource<?>> loadPropertySources(MatrixConfigDataSource matrixConfigDataSource) {
        // 已经加载过
        if (null != this.configRootURI) {
            PrettyPrinter.INSTANCE.buffer("configRoot is loaded:{}", this.configRootURI);
            return Collections.emptyList();
        }

        this.activeProfiles = matrixConfigDataSource.getActiveProfiles();
        PrettyPrinter.INSTANCE.buffer("activeProfiles is: {}", activeProfiles);
        String profile = this.activeProfiles.isEmpty() ? CONFIG_DIRECTORY
                : CONFIG_DIRECTORY.concat(Symbol.HYPHEN.getSymbol()).concat(this.activeProfiles.get(0));
        PrettyPrinter.INSTANCE.buffer("actived profile directory: {}", profile);
        String resolvedConfigRoot = populateConfigRoot();
        if (null == resolvedConfigRoot) {
            resolvedConfigRoot = matrixConfigDataSource.getConfigRoot();
            PrettyPrinter.INSTANCE.buffer("'configRoot' is found in 'spring.config.import=matrix://' : {}", resolvedConfigRoot);
        }
        resolvedConfigRoot = resolvedConfigRoot.endsWith(Symbol.URI_SEPARATOR.toString()) ? resolvedConfigRoot : resolvedConfigRoot.concat(Symbol.URI_SEPARATOR.getSymbol());
        resolvedConfigRoot = resolvedConfigRoot.concat(profile).concat(Symbol.URI_SEPARATOR.getSymbol());
        populateConfigRoot(resolvedConfigRoot);
        // classpath不存在继续寻找matrix://
        if (null == this.configRootURI) {
            PrettyPrinter.INSTANCE.buffer("'configRoot' isn't found in 'classpath',retry...");
            return Collections.emptyList();
        }
        PrettyPrinter.INSTANCE.buffer("final 'configRoot' is: {}", this.configRootURL);
        List<PropertySource<?>> propertySources = resolvePropertySourcesPattern(this.configRootURL.toString());
        PrettyPrinter.INSTANCE.flush();
        return propertySources;
    }

    private void populateConfigRoot(String resolvedConfigRoot) {
        if (!resolvedConfigRoot.startsWith(CLASSPATH)) {
            this.configRootURI = URIUtil.INSTANCE.toURI(resolvedConfigRoot);
            this.configRootURL = URIUtil.INSTANCE.toURL(resolvedConfigRoot);
            return;
        }
        // start with classpath
        try {
            Resource[] resources = resourcePatternResolver.getResources(resolvedConfigRoot);
            if (resources.length > 0) {
                this.configRootURI = resources[0].getURI();
                this.configRootURL = resources[0].getURL();
            }
        } catch (IOException e) {
            throw new MatrixWarnException(e);
        }
    }

    private List<PropertySource<?>> resolvePropertySourcesPattern(String resourceLocationPatternPrefix) {
        List<PropertySource<?>> propertySources = new ArrayList<>();
        propertySources.addAll(resolvePropertySources(resourceLocationPatternPrefix + JDBC_PATTERN, JDBC_PREFIX));
        propertySources.addAll(resolvePropertySources(resourceLocationPatternPrefix + LOGGER_PATTERN, LOGGER_PREFIX));
        // propertySources.addAll(resolvePropertySources(resourceLocationPatternPrefix + AUTOSCAN_PATTERN, AUTOSCAN_PREFIX));
        return propertySources;
    }

    private List<PropertySource<?>> resolvePropertySources(String resourceLocationPattern, String propertySourceName) {
        Resource[] resources;
        try {
            resources = resourcePatternResolver.getResources(resourceLocationPattern);
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
        if (resources.length == 0) {
            throw new MatrixWarnException("No files match the pattern: {}", resourceLocationPattern);
        }
        for (Resource resource : resources) {
            return resolvePropertySource(resource, propertySourceName);
        }
        return Collections.emptyList();
    }

    private List<PropertySource<?>> resolvePropertySource(Resource resource, String propertySourceName) {
        String filename = resource.getFilename();
        try {
            if (filename.endsWith(YAML) || filename.endsWith(YML)) {
                PrettyPrinter.INSTANCE.buffer("load yaml file: {}", filename);
                return yamlPropertySourceLoader.load(propertySourceName, resource);
            }
            PrettyPrinter.INSTANCE.buffer("load property file: {}", filename);
            return propertiesPropertySourceLoader.load(propertySourceName, resource);
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }
}
