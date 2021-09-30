package liangchen.wang.matrix.framework.commons.configuration;

import liangchen.wang.matrix.framework.commons.exception.AssertUtil;
import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;
import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;
import liangchen.wang.matrix.framework.commons.file.FileUtil;
import liangchen.wang.matrix.framework.commons.network.URIUtil;
import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @author Liangchen.Wang 2021-09-30 11:07
 */
public final class ConfigurationBuilder {
    private final static String XML = ".xml";
    private final static String PROPERTIES = ".properties";
    private final static String YML = ".yml";
    private final static String YAML = ".yaml";
    private final static String JSON = ".json";

    private URI baseUri;

    private ConfigurationBuilder(URI baseUri) {
        this.baseUri = baseUri;
    }

    public static ConfigurationBuilder newInstance(URI baseUri) {
        return new ConfigurationBuilder(baseUri);
    }

    public static ConfigurationBuilder newInstance(String baseUri, String... more) {
        return new ConfigurationBuilder(URIUtil.INSTANCE.toURI(baseUri, more));
    }

    public String getBaseUri() {
        return baseUri.toString();
    }

    public URI getURI(String configurationFileName) {
        return baseUri.resolve(configurationFileName);
    }

    public URL getURL(String configurationFileName) {
        return URIUtil.INSTANCE.toURL(getURI(configurationFileName));
    }

    public String getPath(String configurationFileName) {
        return getURL(configurationFileName).toString();
    }

    public boolean exists(String configurationFileName) {
        return URIUtil.INSTANCE.isAvailableURL(getURL(configurationFileName));
    }

    public Configuration build(String configurationFileName) {
        return build(configurationFileName, null, 0);
    }

    private Configuration build(String configurationFileName, TimeUnit timeUnit, long reloadPeriod) {
        AssertUtil.INSTANCE.notBlank(configurationFileName, "The name of configuration file can't be blank");
        String extension = FileUtil.INSTANCE.extension(configurationFileName).toLowerCase();
        ReloadingFileBasedConfigurationBuilder<?> builder = null;
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
                throw new MatrixInfoException();
        }
        FileBasedBuilderParameters fileBasedBuilderParameters = new Parameters().fileBased().setListDelimiterHandler(new DefaultListDelimiterHandler(','));
        URL url = getURL(configurationFileName);
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
