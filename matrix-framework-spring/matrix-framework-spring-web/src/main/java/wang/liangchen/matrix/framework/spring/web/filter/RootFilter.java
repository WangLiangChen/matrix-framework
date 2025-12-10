package wang.liangchen.matrix.framework.spring.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import wang.liangchen.matrix.framework.commons.runtime.LocaleTimeZoneContext;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.spring.web.context.WebContext;
import wang.liangchen.matrix.framework.spring.web.utils.CookieUtil;

import java.io.IOException;
import java.util.Locale;

@Component
public class RootFilter extends OncePerRequestFilter implements OrderedFilter {
    private final static String[] LOCALE_PARAMS = new String[]{"locale", "lang", "language"};
    // private final static String[] TIMEZONE_PARAMS = new String[]{"TimeZone", "timeZone", "timezone"};

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestId = request.getParameter(WebContext.REQUEST_ID);
        if (StringUtil.INSTANCE.isNullOrBlank(requestId)) {
            requestId = request.getHeader(WebContext.REQUEST_ID);
        }
        // Set request id to thread local
        WebContext.INSTANCE.setRequestId(requestId);
        // Set request id to response header
        response.setHeader(WebContext.REQUEST_ID, requestId);
        // Set Locale
        Locale locale = resolveRequestLocale(request);
        LocaleTimeZoneContext.INSTANCE.setLocale(locale);
        try {
            filterChain.doFilter(request, response);
        } finally {
            WebContext.INSTANCE.remove();
            LocaleTimeZoneContext.INSTANCE.remove();
        }
    }

    private Locale resolveRequestLocale(HttpServletRequest request) {
        // from query
        for (String param : LOCALE_PARAMS) {
            String languageTag = request.getParameter(param);
            if (null != languageTag && !languageTag.isBlank()) {
                return Locale.forLanguageTag(languageTag);
            }
        }
        // from header
        for (String param : LOCALE_PARAMS) {
            String languageTag = request.getHeader(param);
            if (null != languageTag && !languageTag.isBlank()) {
                return Locale.forLanguageTag(languageTag);
            }
        }
        // from cookie
        for (String param : LOCALE_PARAMS) {
            String languageTag = CookieUtil.INSTANCE.getCookieValue(request, param);
            if (null != languageTag && !languageTag.isBlank()) {
                return Locale.forLanguageTag(languageTag);
            }
        }
        return request.getLocale();
    }
}
