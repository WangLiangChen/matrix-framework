package wang.liangchen.matrix.framework.commons.runtime;

import java.util.Locale;
import java.util.TimeZone;

public enum LocaleTimeZoneContext {
    INSTANCE;
    private static final ThreadLocal<LocaleTimeZone> container = new ThreadLocal<>();
    private static final ThreadLocal<LocaleTimeZone> inheritableContainer = new InheritableThreadLocal<>();

    public void reset() {
        container.remove();
        inheritableContainer.remove();
    }

    public Locale getLocale() {
        return getLocaleTimeZone().getLocale();
    }

    public TimeZone getTimeZone() {
        return getLocaleTimeZone().getTimeZone();
    }

    public LocaleTimeZone getLocaleTimeZone() {
        LocaleTimeZone localeTimeZone = container.get();
        if (null == localeTimeZone) {
            localeTimeZone = inheritableContainer.get();
        }
        if (null == localeTimeZone) {
            localeTimeZone = new LocaleTimeZone(Locale.getDefault(), TimeZone.getDefault());
        }
        return localeTimeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        setTimeZone(timeZone, false);
    }

    public void setTimeZone(TimeZone timeZone, boolean inheritable) {
        setLocaleTimeZone(null, timeZone, inheritable);
    }

    public void setLocale(Locale locale) {
        setLocale(locale, false);
    }

    public void setLocale(Locale locale, boolean inheritable) {
        setLocaleTimeZone(locale, null, inheritable);
    }

    public void setLocaleTimeZone(Locale locale, TimeZone timeZone) {
        setLocaleTimeZone(locale, timeZone, false);
    }

    public void setLocaleTimeZone(Locale locale, TimeZone timeZone, boolean inheritable) {
        LocaleTimeZone localeTimeZone = getLocaleTimeZone();
        if (null == locale) {
            locale = localeTimeZone.getLocale();
        }
        if (null == timeZone) {
            timeZone = localeTimeZone.getTimeZone();
        }

        if (inheritable) {
            inheritableContainer.set(new LocaleTimeZone(locale, timeZone));
            container.remove();
            return;
        }
        container.set(new LocaleTimeZone(locale, timeZone));
        inheritableContainer.remove();
    }

}
