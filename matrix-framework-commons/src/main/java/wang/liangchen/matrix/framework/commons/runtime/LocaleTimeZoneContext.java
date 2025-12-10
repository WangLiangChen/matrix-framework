package wang.liangchen.matrix.framework.commons.runtime;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public enum LocaleTimeZoneContext {
    INSTANCE;
    private static final TransmittableThreadLocal<Map<String, Object>> threadLocal = TransmittableThreadLocal.withInitial(HashMap::new);
    public static final String LOCALE = "LOCALE";
    public static final String TIMEZONE = "TIMEZONE";

    public void remove() {
        threadLocal.remove();
    }

    public Locale getLocale() {
        return (Locale) threadLocal.get().get(LOCALE);
    }

    public TimeZone getTimeZone() {
        return (TimeZone) threadLocal.get().get(TIMEZONE);
    }

    public void setLocale(Locale locale) {
        threadLocal.get().put(LOCALE, locale);
    }

    public void setTimeZone(TimeZone timeZone) {
        threadLocal.get().put(TIMEZONE, timeZone);
    }
}
