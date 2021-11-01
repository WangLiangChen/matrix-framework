package wang.liangchen.matrix.framework.springboot.context;

import wang.liangchen.matrix.framework.commons.configuration.ConfigurationResolver;
import org.apache.commons.configuration2.Configuration;

import java.net.URI;
import java.net.URL;

/**
 * @author Liangchen.Wang
 */
public enum ConfigurationContext {
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

    public URL getURL(String relativePath) {
        return this.configurationResolver.getURL(relativePath);
    }

    public boolean exists(String relativePath) {
        return this.configurationResolver.exists(relativePath);
    }

}
