package wang.liangchen.matrix.framework.web.context;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-06-30 1:15
 */
public enum WebContext {
    //
    INSTANCE;
    private final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);
    public static final String REQUEST_ID = "requestId";
    public static final String LOCALE = "locale";

    public void setRequestId(String requestId) {
        threadLocal.get().put(REQUEST_ID, requestId);
    }

    public void setLocale(Locale locale) {
        threadLocal.get().put(LOCALE, locale);
    }

    public String getRequestId() {
        return (String) threadLocal.get().getOrDefault(REQUEST_ID, Symbol.BLANK.getSymbol());
    }

    public Locale getLocale() {
        return (Locale) threadLocal.get().getOrDefault(LOCALE, Locale.getDefault());
    }

    public void remove() {
        threadLocal.remove();
    }
}
