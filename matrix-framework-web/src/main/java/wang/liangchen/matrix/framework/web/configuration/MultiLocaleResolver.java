package wang.liangchen.matrix.framework.web.configuration;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author Liangchen.Wang
 */
@Component("localeResolver")
public class MultiLocaleResolver extends AcceptHeaderLocaleResolver {
    private final static String[] params = new String[]{"locale", "lang"};

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = doResolveLocale(request);
        if (null != locale) {
            LocaleContextHolder.setLocale(locale);
        }
        return locale;
        // LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
    }

    private Locale doResolveLocale(HttpServletRequest request) {
        // 依次获取 后面的覆盖前面的
        String localeString = null;
        for (String param : params) {
            localeString = request.getParameter(param);
        }
        for (String param : params) {
            localeString = request.getHeader(param);
        }
        if (StringUtil.INSTANCE.isBlank(localeString)) {
            return super.resolveLocale(request);
        }
        Locale requestLocale = Locale.forLanguageTag(localeString);
        if (null == requestLocale) {
            return super.resolveLocale(request);
        }
        return requestLocale;
    }

}
