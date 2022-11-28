package wang.liangchen.matrix.framework.web.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.web.utils.CookieUtil;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * @author Liangchen.Wang
 */
@Component("localeResolver")
public class MultiLocaleResolver extends AcceptHeaderLocaleResolver {
    private final static String[] params = new String[]{"locale", "lang"};
    private final List<Locale> supportedLocales = new ArrayList<>(4);


    /**
     * Configure supported locales to check against the requested locales
     * determined via {@link HttpServletRequest#getLocales()}. If this is not
     * configured then {@link HttpServletRequest#getLocale()} is used instead.
     *
     * @param locales the supported locales
     * @since 4.3
     */
    public void setSupportedLocales(List<Locale> locales) {
        this.supportedLocales.clear();
        this.supportedLocales.addAll(locales);
    }

    /**
     * Get the configured list of supported locales.
     *
     * @since 4.3
     */
    public List<Locale> getSupportedLocales() {
        return this.supportedLocales;
    }


    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale requestLocale = resolveRequestLocale(request);
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale != null && requestLocale == null) {
            return defaultLocale;
        }

        List<Locale> supportedLocales = getSupportedLocales();
        if (supportedLocales.isEmpty() || supportedLocales.contains(requestLocale)) {
            return requestLocale;
        }
        Locale supportedLocale = findSupportedLocale(request, supportedLocales);
        if (supportedLocale != null) {
            return supportedLocale;
        }
        return (defaultLocale != null ? defaultLocale : requestLocale);
    }

    private Locale resolveRequestLocale(HttpServletRequest request) {
        // 级别最高是自定义Header
        for (String param : params) {
            String parameter = request.getHeader(param);
            if (StringUtil.INSTANCE.isNotBlank(parameter)) {
                Locale locale = Locale.forLanguageTag(parameter);
                if (null != locale) {
                    return locale;
                }
            }
        }
        // 其次是自定义parameter
        for (String param : params) {
            String parameter = request.getParameter(param);
            if (StringUtil.INSTANCE.isNotBlank(parameter)) {
                Locale locale = Locale.forLanguageTag(parameter);
                if (null != locale) {
                    return locale;
                }
            }
        }
        // 然后是Cookie
        for (String param : params) {
            String parameter = CookieUtil.INSTANCE.getCookieValue(request, param);
            if (StringUtil.INSTANCE.isNotBlank(parameter)) {
                Locale locale = Locale.forLanguageTag(parameter);
                if (null != locale) {
                    return locale;
                }
            }
        }
        // 最次才是默认的
        if (request.getHeader("Accept-Language") != null) {
            return request.getLocale();
        }
        return null;
    }

    @Nullable
    private Locale findSupportedLocale(HttpServletRequest request, List<Locale> supportedLocales) {
        Enumeration<Locale> requestLocales = request.getLocales();
        Locale languageMatch = null;
        while (requestLocales.hasMoreElements()) {
            Locale locale = requestLocales.nextElement();
            if (supportedLocales.contains(locale)) {
                if (languageMatch == null || languageMatch.getLanguage().equals(locale.getLanguage())) {
                    // Full match: language + country, possibly narrowed from earlier language-only match
                    return locale;
                }
            } else if (languageMatch == null) {
                // Let's try to find a language-only match as a fallback
                for (Locale candidate : supportedLocales) {
                    if (!StringUtils.hasLength(candidate.getCountry()) &&
                            candidate.getLanguage().equals(locale.getLanguage())) {
                        languageMatch = candidate;
                        break;
                    }
                }
            }
        }
        return languageMatch;
    }

    @Override
    public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        throw new UnsupportedOperationException(
                "Cannot change HTTP Accept-Language header - use a different locale resolution strategy");
    }

}
