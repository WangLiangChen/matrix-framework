package wang.liangchen.matrix.framework.web.locale;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.web.utils.CookieUtil;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * @author Liangchen.Wang
 */
@Component("localeResolver")
public class MultiRulesLocaleResolver extends AcceptHeaderLocaleResolver {
    private final static String ACCEPT_LANGUAGE = "Accept-Language";
    private final static String[] params = new String[]{"locale", "lang", "language"};

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
        // 级别最高的是显式指定的parameter
        for (String param : params) {
            String parameter = request.getParameter(param);
            if (StringUtil.INSTANCE.isNotBlank(parameter)) {
                Locale locale = Locale.forLanguageTag(parameter);
                if (null != locale) {
                    return locale;
                }
            }
        }

        // 其次是Header
        for (String param : params) {
            String parameter = request.getHeader(param);
            if (StringUtil.INSTANCE.isNotBlank(parameter)) {
                Locale locale = Locale.forLanguageTag(parameter);
                if (null != locale) {
                    return locale;
                }
            }
        }
        // 再次是Cookie
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
        if (request.getHeader(ACCEPT_LANGUAGE) != null) {
            return request.getLocale();
        }
        return null;
    }

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
}