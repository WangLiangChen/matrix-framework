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
import wang.liangchen.matrix.framework.commons.network.URIUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * @author Liangchen.Wang 2022-06-25 16:07
 */
public enum EnvironmentContext {
    INSTANCE;
    public static final String JDBC_PREFIX = "jdbc";
    public static final String LOGGER_PREFIX = "logger";
    private static final Map<String, String> configFiles = new HashMap<String, String>() {{
        put(JDBC_PREFIX, "jdbc.*");
        put(LOGGER_PREFIX, "logger.*");
    }};
    private static final String CLASSPATH = "classpath";
    private static final String YAML = "yaml";
    private static final String YML = "yml";
    private static final String CONFIG_DIRECTORY = "matrix-framework";
    private final static String CONFIG_ROOT_KEY = "configRoot";
    private static final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static final PropertiesPropertySourceLoader propertiesPropertySourceLoader = new PropertiesPropertySourceLoader();
    private static final YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();

    private URI configRootURI;
    private URL configRootURL;
    private Environment environment;
    private List<String> activeProfiles;
    private String profile;

    void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public URI getConfigRootURI() {
        if (null == configRootURI) {
            throw new MatrixErrorException("configRoot has not been initialized");
        }
        return configRootURI;
    }

    public URL getConfigRootURL() {
        if (null == configRootURL) {
            throw new MatrixErrorException("configRoot has not been initialized");
        }
        return configRootURL;
    }

    public URI getURI(String relativePath) {
        return this.configRootURI.resolve(relativePath);
    }

    public URL getURL(String relativePath) {
        return URIUtil.INSTANCE.expendURL(this.configRootURL, relativePath);
    }

    /**
     * 该方法会被ConfigDataLoader调用
     * 没有配置 spring.config.import=matrix://xxx. 会调用1次，参数为classpath:
     * 配置了  spring.config.import=matrix://xxx. 会调用2次，参数分别为classpath:,xxx
     *
     * @param matrixConfigDataSource activeProfiles and configRoot
     * @return PropertySources
     */
    List<PropertySource<?>> loadPropertySources(MatrixConfigDataSource matrixConfigDataSource) {
        if (null != this.configRootURI) {
            PrettyPrinter.INSTANCE.buffer("configRoot has been assigned, {}", this.configRootURI);
            return Collections.emptyList();
        }
        String importPath = matrixConfigDataSource.getConfigRoot();
        List<String> activeProfiles = matrixConfigDataSource.getActiveProfiles();
        // 第一次调用 处理配置的systemPath,
        if (importPath.startsWith(CLASSPATH)) {
            String systemPath = resolveSystemPath();
            if (StringUtil.INSTANCE.isNotBlank(systemPath)) {
                systemPath = buildProfilePath(systemPath, activeProfiles);
                PrettyPrinter.INSTANCE.flush();
                return resolveAllPropertySource(systemPath);
            }
            String classPath = buildProfilePath(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX, activeProfiles);
            PrettyPrinter.INSTANCE.flush();
            return resolveAllPropertySource(classPath);
        }
        // 第二次调用(如果有显式配置的spring.config.import=matrix://)，处理importPath
        importPath = buildProfilePath(importPath, activeProfiles);
        PrettyPrinter.INSTANCE.flush();
        return resolveAllPropertySource(importPath);
    }

    private String buildProfilePath(String path, List<String> activeProfiles) {
        // append matrix-frameword and profile
        if (null == this.profile) {
            this.activeProfiles = activeProfiles;
            PrettyPrinter.INSTANCE.buffer("activeProfiles is: {}", activeProfiles);
            this.profile = this.activeProfiles.isEmpty() ? CONFIG_DIRECTORY
                    : CONFIG_DIRECTORY.concat(Symbol.HYPHEN.getSymbol()).concat(this.activeProfiles.get(0));
            PrettyPrinter.INSTANCE.buffer("active profile path: {}", profile);
        }
        path = path.endsWith(Symbol.URI_SEPARATOR.getSymbol()) ? path : path.concat(Symbol.URI_SEPARATOR.getSymbol());
        path = path.concat(this.profile).concat(Symbol.URI_SEPARATOR.getSymbol());
        PrettyPrinter.INSTANCE.buffer("Resolved Profile path: {}", path);
        return path;
    }

    private String resolveSystemPath() {
        String configRoot = System.getenv(CONFIG_ROOT_KEY);
        if (StringUtil.INSTANCE.isNotBlank(configRoot)) {
            PrettyPrinter.INSTANCE.buffer("'configRoot' is found in 'System.getenv' : {}", configRoot);
            return configRoot;
        }
        PrettyPrinter.INSTANCE.buffer("'configRoot' isn't found in 'System.getenv'");
        configRoot = System.getProperty(CONFIG_ROOT_KEY);
        if (StringUtil.INSTANCE.isNotBlank(configRoot)) {
            PrettyPrinter.INSTANCE.buffer("'configRoot' is found in 'System.getProperty' : {}", configRoot);
            return configRoot;
        }
        PrettyPrinter.INSTANCE.buffer("'configRoot' isn't found in 'EnvironmentVariable'");
        return null;
    }

    private List<PropertySource<?>> resolveAllPropertySource(String resourceLocationPatternPrefix) {
        Map<String, Resource[]> resourceMap = resolveAllResources(resourceLocationPatternPrefix);
        List<PropertySource<?>> propertySources = new ArrayList<>();
        for (Map.Entry<String, Resource[]> entry : resourceMap.entrySet()) {
            for (Resource resource : entry.getValue()) {
                if (null == this.configRootURI) {
                    resolveConfigRoot(resource);
                }
                propertySources.addAll(resolvePropertySource(resource, entry.getKey()));
            }
        }
        return propertySources;
    }

    private void resolveConfigRoot(Resource resource) {
        try {
            String uri = resource.getURI().toString();
            uri = uri.substring(0, uri.lastIndexOf('/'));
            this.configRootURI = URIUtil.INSTANCE.toURI(uri);
            this.configRootURL = URIUtil.INSTANCE.toURL(uri);
            PrettyPrinter.INSTANCE.buffer("Final configRoot: {}", this.configRootURI);
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
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

    private Map<String, Resource[]> resolveAllResources(String resourceLocationPatternPrefix) {
        if (!resourceLocationPatternPrefix.startsWith(CLASSPATH)) {
            resourceLocationPatternPrefix = URIUtil.INSTANCE.toURL(resourceLocationPatternPrefix).toString();
        }
        Map<String, Resource[]> resourceMap = new HashMap<>();
        for (Map.Entry<String, String> entry : configFiles.entrySet()) {
            Resource[] resources = resolveResources(resourceLocationPatternPrefix + entry.getValue());
            resourceMap.put(entry.getKey(), resources);
        }
        return resourceMap;
    }

    private Resource[] resolveResources(String resourceLocationPattern) {
        try {
            return resourcePatternResolver.getResources(resourceLocationPattern);
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }
}
