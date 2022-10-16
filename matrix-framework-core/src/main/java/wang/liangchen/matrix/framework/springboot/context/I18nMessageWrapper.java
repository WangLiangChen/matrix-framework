package wang.liangchen.matrix.framework.springboot.context;

import wang.liangchen.matrix.framework.commons.runtime.MessageWrapper;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import java.util.Locale;

/**
 * @author Liangchen.Wang 2022-10-16 11:11
 */
public class I18nMessageWrapper extends MessageWrapper {

    private I18nMessageWrapper(String i18n, Locale locale, String code, String message) {
        super(i18n, locale, code, message);
    }

    public static MessageWrapper of(String code, String message, Object... args) {
        String i18nMessage = StringUtil.INSTANCE.format(message, args);
        i18nMessage = MessageSourceLoader.INSTANCE.getMessage(i18nMessage, args);
        return new I18nMessageWrapper(message, MessageSourceLoader.INSTANCE.getLocale(), code, i18nMessage);
    }

    public static MessageWrapper of(String message, Object... args) {
        return of(null, message, args);
    }
}
