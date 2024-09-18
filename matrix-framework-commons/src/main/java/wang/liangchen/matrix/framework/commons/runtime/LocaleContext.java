package wang.liangchen.matrix.framework.commons.runtime;

import java.util.Locale;

public enum LocaleContext {
    INSTANCE;
    private static final ThreadLocal<Locale> localeContextHolder = new ThreadLocal<>();
    private static final ThreadLocal<Locale> inheritableLocaleContextHolder = new InheritableThreadLocal<>();

    public void reset() {
        localeContextHolder.remove();
        inheritableLocaleContextHolder.remove();
    }

    public void setLocale(Locale locale) {
        setLocale(locale, false);
    }

    public void setLocale(Locale locale, boolean inheritable) {
        if (locale == null) {
            reset();
            return;
        }
        if (inheritable) {
            inheritableLocaleContextHolder.set(locale);
            localeContextHolder.remove();
            return;
        }
        localeContextHolder.set(locale);
        inheritableLocaleContextHolder.remove();
    }

    public Locale getLocale() {
        Locale locale = localeContextHolder.get();
        if (locale == null) {
            locale = inheritableLocaleContextHolder.get();
        }
        return locale;
    }

}
