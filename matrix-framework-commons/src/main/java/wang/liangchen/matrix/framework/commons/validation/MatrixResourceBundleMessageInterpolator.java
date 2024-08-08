package wang.liangchen.matrix.framework.commons.validation;


import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import wang.liangchen.matrix.framework.commons.StringUtil;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-10-12 8:54
 */
public class MatrixResourceBundleMessageInterpolator extends ResourceBundleMessageInterpolator {
    public MatrixResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, Set<Locale> locales, Locale defaultLocale, LocaleResolver localeResolver, boolean preloadResourceBundles) {
        super(userResourceBundleLocator, locales, defaultLocale, localeResolver, preloadResourceBundles);
    }

    @Override
    public String interpolate(String message, Context context) {
        String result = super.interpolate(message, context);
        if (StringUtil.INSTANCE.isISO_8859_1(result)) {
            return new String(result.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        return result;
    }

    @Override
    protected String interpolate(Context context, Locale locale, String term) {
        String result = super.interpolate(context, locale, term);
        if (StringUtil.INSTANCE.isISO_8859_1(result)) {
            return new String(result.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        return result;
    }

}
