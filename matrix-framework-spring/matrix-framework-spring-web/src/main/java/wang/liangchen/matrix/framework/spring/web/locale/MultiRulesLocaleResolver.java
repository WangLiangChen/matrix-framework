package wang.liangchen.matrix.framework.spring.web.locale;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import wang.liangchen.matrix.framework.commons.runtime.LocaleTimeZoneContext;

import java.util.Locale;

/**
 * @author Liangchen.Wang
 */
@Component("localeResolver")
public class MultiRulesLocaleResolver extends AcceptHeaderLocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        return LocaleTimeZoneContext.INSTANCE.getLocale();
    }
}