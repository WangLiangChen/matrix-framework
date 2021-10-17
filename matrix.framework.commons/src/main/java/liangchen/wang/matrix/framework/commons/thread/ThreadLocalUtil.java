package liangchen.wang.matrix.framework.commons.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang
 */
public enum ThreadLocalUtil {
    INSTANCE;
    private final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(() -> new HashMap<>());

    public Map<String, Object> getContainer() {
        return threadLocal.get();
    }

    public void put(String key, Object value) {
        threadLocal.get().put(key, value);
    }

    public void remove(String key) {
        threadLocal.get().remove(key);
    }

    public void clear() {
        threadLocal.get().clear();
    }

    public void remove() {
        threadLocal.remove();
    }
}
