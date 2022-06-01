package wang.liangchen.matrix.framework.springboot.context;

import org.apache.commons.configuration2.Configuration;
import wang.liangchen.matrix.framework.commons.configuration.ConfigurationResolver;

import java.net.URI;
import java.net.URL;

/**
 * @author Liangchen.Wang
 * 配置文件上下文 适配外部配置处理器ConfigurationResolver
 * 在Spring容器启动早期被初始化
 */
public enum ConfigurationContext {
    /**
     * instance;
     */
    INSTANCE;
    private final ConfigurationResolver configurationResolver = ConfigurationResolver.newInstance();

    public void setBaseUri(String uriString) {
        this.configurationResolver.setBaseUriString(uriString);
    }

    public Configuration resolve(String relativePath) {
        return this.configurationResolver.resolve(relativePath);
    }

    public URI getBaseURI() {
        return this.configurationResolver.getBaseUri();
    }

    public URI getURI(String relativePath) {
        return this.configurationResolver.getURI(relativePath);
    }

    public URL getURL(String relativePath) {
        return this.configurationResolver.getURL(relativePath);
    }

    public boolean exists(String relativePath) {
        return this.configurationResolver.exists(relativePath);
    }

}
