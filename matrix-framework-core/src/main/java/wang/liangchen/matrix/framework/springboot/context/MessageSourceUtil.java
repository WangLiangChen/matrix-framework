package wang.liangchen.matrix.framework.springboot.context;

import org.springframework.context.MessageSource;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;

/**
 * @author LiangChen.Wang
 * 在spring容器启动早期即完成初始化
 */
public enum MessageSourceUtil {
    /**
     * instance;
     */
    INSTANCE;
    private MessageSource messageSource;

    public void resetMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String messageKey, String defaultMessage, Locale locale, Object... args) {
        return this.messageSource.getMessage(messageKey, args, defaultMessage, locale);
    }

    public String getMessage(String messageKey, Locale locale, Object... args) {
        return getMessage(messageKey, StringUtil.INSTANCE.blankString(), locale, args);
    }

    public String getMessage(String messageKey, String defaultMessage, Object... args) {
        return getMessage(messageKey, defaultMessage, ValidationUtil.INSTANCE.getLocale(), args);
    }

    public String getMessage(String messageKey, Object... args) {
        return getMessage(messageKey, ValidationUtil.INSTANCE.getLocale(), args);
    }

    public Locale getLocale() {
        return ValidationUtil.INSTANCE.getLocale();
    }

}
