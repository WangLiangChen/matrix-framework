package wang.liangchen.matrix.framework.web.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Liangchen.Wang 2022-06-30 1:15
 */
public enum WebContext {
    //
    INSTANCE;
    private final TransmittableThreadLocal<Map<String, Object>> threadLocal = TransmittableThreadLocal.withInitial(HashMap::new);
    public static final String REQUEST_ID = "requestId";

    public void setRequestId(String requestId) {
        threadLocal.get().put(REQUEST_ID, requestId);
    }

    public String getRequestId() {
        return (String) threadLocal.get().get(REQUEST_ID);
    }

    public void remove() {
        threadLocal.remove();
    }
}