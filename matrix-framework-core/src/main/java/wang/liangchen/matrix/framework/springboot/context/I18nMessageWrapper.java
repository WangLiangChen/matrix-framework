package wang.liangchen.matrix.framework.springboot.context;

import wang.liangchen.matrix.framework.commons.runtime.MessageWrapper;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;

/**
 * @author Liangchen.Wang 2023-05-04 21:37
 */
public class I18nMessageWrapper extends MessageWrapper {
    private I18nMessageWrapper(String i18n, Locale locale, String code, String message) {
        super(i18n, locale, code, message);
    }

    public static MessageWrapper of(String i18n, Locale locale, String code, Object... args) {
        return new I18nMessageWrapper(i18n, locale, code, MessageSourceUtil.INSTANCE.getMessage(i18n, locale, args));
    }

    public static MessageWrapper of(String i18n, Locale locale, Object... args) {
        return of(i18n, locale, null, args);
    }

    public static MessageWrapper of(String i18n, String code, Object... args) {
        return of(i18n, ValidationUtil.INSTANCE.getOrDefaultLocale(), code, args);
    }

    public static MessageWrapper of(String i18n, Object... args) {
        return of(i18n, ValidationUtil.INSTANCE.getOrDefaultLocale(), null, args);
    }
}
