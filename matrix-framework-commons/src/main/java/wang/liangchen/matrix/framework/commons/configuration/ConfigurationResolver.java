package wang.liangchen.matrix.framework.commons.configuration;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.file.FileUtil;
import wang.liangchen.matrix.framework.commons.network.URIUtil;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @author Liangchen.Wang 2021-09-30 11:07
 */
public final class ConfigurationResolver {
    private final static String XML = "xml";
    private final static String PROPERTIES = "properties";
    private final static String YML = "yml";
    private final static String YAML = "yaml";
    private final static String JSON = "json";

    private URI baseUri;

    private ConfigurationResolver(URI baseUri) {
        this.baseUri = baseUri;
    }

    private ConfigurationResolver() {
    }

    public static ConfigurationResolver newInstance(URI baseUri) {
        return new ConfigurationResolver(baseUri);
    }

    public static ConfigurationResolver newInstance(String uriString) {
        URI baseUri = URIUtil.INSTANCE.toURI(uriString);
        return new ConfigurationResolver(baseUri);
    }

    public URI getBaseUri() {
        return baseUri;
    }

    public String getBaseUriString() {
        return baseUri.toString();
    }

    public URL getBaseUrl() {
        return URIUtil.INSTANCE.toURL(this.baseUri);
    }

    public String getBaseUrlString() {
        URL url = URIUtil.INSTANCE.toURL(this.baseUri);
        return url.toString();
    }

    public URI getURI(String relativePath) {
        return URIUtil.INSTANCE.expandURI(this.baseUri, relativePath);
    }

    public URL getURL(String relativePath) {
        return URIUtil.INSTANCE.toURL(getURI(relativePath));
    }

    public String getURIString(String relativePath) {
        return getURI(relativePath).toString();
    }

    public boolean exists(String relativePath) {
        return URIUtil.INSTANCE.isAvailableURL(getURL(relativePath));
    }

    public Configuration resolve(String relativePath) {
        return resolve(relativePath, null, 0);
    }

    private Configuration resolve(String relativePath, TimeUnit timeUnit, long reloadPeriod) {
        Assert.INSTANCE.notBlank(relativePath, "The relativePath of configuration file can't be blank");
        String extension = FileUtil.INSTANCE.extension(relativePath).toLowerCase();
        ReloadingFileBasedConfigurationBuilder<?> builder;
        switch (extension) {
            case PROPERTIES:
                builder = new ReloadingFileBasedConfigurationBuilder<>(PropertiesConfiguration.class);
                break;
            case YAML:
            case YML:
                builder = new ReloadingFileBasedConfigurationBuilder<>(YAMLConfiguration.class);
                break;
            case JSON:
                builder = new ReloadingFileBasedConfigurationBuilder<>(JSONConfiguration.class);
                break;
            case XML:
                builder = new ReloadingFileBasedConfigurationBuilder<>(XMLConfiguration.class);
                break;
            default:
                throw new MatrixInfoException("File extension not applicable : ", extension);
        }
        FileBasedBuilderParameters fileBasedBuilderParameters = new Parameters().fileBased();
        //.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
        URL url = getURL(relativePath);
        fileBasedBuilderParameters = fileBasedBuilderParameters.setURL(url);
        builder.configure(fileBasedBuilderParameters);
        if (reloadPeriod > 0L && null != timeUnit) {
            PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(), null, reloadPeriod, timeUnit);
            trigger.start();
        }
        try {
            return builder.getConfiguration();
        } catch (ConfigurationException e) {
            throw new MatrixErrorException(e);
        }
    }

}
