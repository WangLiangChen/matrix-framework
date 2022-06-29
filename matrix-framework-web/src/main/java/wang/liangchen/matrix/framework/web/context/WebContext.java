package wang.liangchen.matrix.framework.web.context;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-06-30 1:15
 */
public enum WebContext {
    //
    INSTANCE;
    private final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);
    public static final String REQUEST_ID = "requestId";

    public void setRequestId(String requestId) {
        threadLocal.get().put(REQUEST_ID, requestId);
    }

    public String getRequestId() {
        String requestId = String.valueOf(threadLocal.get().get(REQUEST_ID));
        return null == requestId ? Symbol.BLANK.getSymbol() : requestId;
    }
    public void remove(){
        threadLocal.remove();
    }
}
