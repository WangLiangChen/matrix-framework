package wang.liangchen.matrix.framework.commons.runtime;

import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Liangchen.Wang
 */
public final class LocaleTimeZone {
    private final Locale locale;
    private final TimeZone timeZone;

    public LocaleTimeZone(Locale locale, TimeZone timeZone) {
        this.locale = locale;
        this.timeZone = timeZone;
    }

    public Locale getLocale() {
        return locale;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }
}
