package wang.liangchen.matrix.framework.commons.validation;

import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolverContext;

import java.util.List;
import java.util.Locale;

/**
 * @author Liangchen.Wang 2022-09-15 9:35
 */
public class MatrixLocaleResolver implements LocaleResolver {
    @Override
    public Locale resolve(LocaleResolverContext localeResolverContext) {
        // get the locales supported by the client from the Accept-Language header
        String acceptLanguageHeader = "it-IT;q=0.9,en-US;q=0.7";
        List<Locale.LanguageRange> acceptedLanguages = Locale.LanguageRange.parse(acceptLanguageHeader);
        List<Locale> resolvedLocales = Locale.filter(acceptedLanguages, localeResolverContext.getSupportedLocales());
        if (resolvedLocales.isEmpty()) {
            return localeResolverContext.getDefaultLocale();
        }
        return resolvedLocales.get(0);
    }
}
