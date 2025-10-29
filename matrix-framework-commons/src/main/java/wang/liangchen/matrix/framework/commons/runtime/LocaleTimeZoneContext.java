package wang.liangchen.matrix.framework.commons.runtime;

import com.alibaba.ttl.TransmittableThreadLocal;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.util.Locale;
import java.util.TimeZone;

public enum LocaleTimeZoneContext {
    INSTANCE;
    private static final TransmittableThreadLocal<LocaleTimeZone> context =
            TransmittableThreadLocal.withInitial(() -> new LocaleTimeZone(Locale.getDefault(), TimeZone.getDefault()));

    public void remove() {
        context.remove();
    }

    public LocaleTimeZone getLocaleTimeZone() {
        LocaleTimeZone localeTimeZone = context.get();
        if (null == localeTimeZone) {
            throw new MatrixErrorException("The LocaleTimeZone has been removed");
        }
        return localeTimeZone;
    }

    public Locale getLocale() {
        return getLocaleTimeZone().getLocale();
    }

    public TimeZone getTimeZone() {
        return getLocaleTimeZone().getTimeZone();
    }

    public void setLocaleTimeZone(LocaleTimeZone localeTimeZone) {
        context.set(localeTimeZone);
    }

    public void setLocaleTimeZone(Locale locale, TimeZone timeZone) {
        if (null == locale) {
            locale = Locale.getDefault();
        }
        if (null == timeZone) {
            timeZone = TimeZone.getDefault();
        }
        setLocaleTimeZone(new LocaleTimeZone(locale, timeZone));
    }
}
