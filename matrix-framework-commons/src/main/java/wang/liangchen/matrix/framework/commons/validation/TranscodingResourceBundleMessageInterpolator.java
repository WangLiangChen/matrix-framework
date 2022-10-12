package wang.liangchen.matrix.framework.commons.validation;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-10-12 8:54
 */
public class TranscodingResourceBundleMessageInterpolator extends ResourceBundleMessageInterpolator {
    public TranscodingResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, Set<Locale> locales, Locale defaultLocale, LocaleResolver localeResolver, boolean preloadResourceBundles) {
        super(userResourceBundleLocator, locales, defaultLocale, localeResolver, preloadResourceBundles);
    }

    @Override
    public String interpolate(String message, Context context) {
        String result = super.interpolate(message, context);
        try {
            return new String(result.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return result;
        }
    }
}
