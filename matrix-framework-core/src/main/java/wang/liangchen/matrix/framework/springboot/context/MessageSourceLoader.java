package wang.liangchen.matrix.framework.springboot.context;

import org.springframework.context.MessageSource;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;

/**
 * @author LiangChen.Wang
 * 在spring容器启动早期即完成初始化
 */
public enum MessageSourceLoader {
    /**
     * instance;
     */
    INSTANCE;
    private MessageSource messageSource;

    public void initMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return this.messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public String getMessage(String code, Object[] args, Locale locale) {
        return this.messageSource.getMessage(code, args, locale);
    }

    public String getMessage(String code, Object[] args, String defaultMessage) {
        return getMessage(code, args, defaultMessage, ValidationUtil.INSTANCE.getLocale());
    }

    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, ValidationUtil.INSTANCE.getLocale());
    }

    public String getMessage(String code, String defaultMessage, Locale locale) {
        return getMessage(code, null, defaultMessage, locale);
    }

    public String getMessage(String code, String defaultMessage) {
        return getMessage(code, null, defaultMessage);
    }

    public String getMessage(String code, Locale locale) {
        return getMessage(code, null, null, locale);
    }

    public String getMessage(String code) {
        return getMessage(code, ValidationUtil.INSTANCE.getLocale());
    }

    public Locale getLocale() {
        return ValidationUtil.INSTANCE.getLocale();
    }

}
