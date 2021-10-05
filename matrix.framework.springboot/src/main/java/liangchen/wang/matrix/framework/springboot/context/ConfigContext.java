package liangchen.wang.matrix.framework.springboot.context;

import liangchen.wang.matrix.framework.commons.configuration.ConfigurationResolver;
import org.apache.commons.configuration2.Configuration;

import java.net.URI;

/**
 * @author Liangchen.Wang
 */
public enum ConfigContext {
    INSTANCE;
    private final ConfigurationResolver configurationResolver = ConfigurationResolver.newInstance();

    public void setBaseUri(String baseUri) {
        this.configurationResolver.setBaseUri(baseUri);
    }

    public void setBaseUri(URI baseUri) {
        this.configurationResolver.setBaseUri(baseUri);
    }

    public Configuration resolve(String relativePath) {
        return this.configurationResolver.resolve(relativePath);
    }

    public URI getURI(String relativePath) {
        return this.configurationResolver.getURI(relativePath);
    }
}
