package wang.liangchen.matrix.framework.web.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author LiangChen.Wang
 */
public enum SseUtil {
    /**
     *
     */
    INSTANCE;
    /**
     * 当前连接数
     */
    private final AtomicInteger count = new AtomicInteger(0);

    /**
     * 使用map对象，便于根据标识flag来获取对应的SseEmitter
     */
    private final Map<SseKey, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public SseEmitter buildEmitter(SseKey sseKey) {
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(sseKey));
        sseEmitter.onError(errorCallBack(sseKey));
        sseEmitter.onTimeout(timeoutCallBack(sseKey));
        sseEmitterMap.put(sseKey, sseEmitter);
        // 数量+1
        count.getAndIncrement();
        return sseEmitter;
    }

    public void unicast(SseKey sseKey, String message) {
        SseEmitter sseEmitter = sseEmitterMap.get(sseKey);
        if (sseEmitter == null) {
            return;
        }
        try {
            sseEmitter.send(message);
        } catch (IOException e) {
            removeEmitter(sseKey);
        }
    }

    public void multicast(String group, String message) {
        Set<SseKey> sseKeys = sseEmitterMap.keySet();
        sseKeys.stream().filter(e -> e.getGroup().equals(group)).forEach(e -> unicast(e, message));
    }

    public void broadcast(String message) {
        Set<SseKey> sseKeys = sseEmitterMap.keySet();
        sseKeys.forEach(e -> unicast(e, message));
    }

    public void removeEmitter(SseKey sseKey) {
        sseEmitterMap.remove(sseKey);
        // 数量-1
        count.getAndDecrement();
    }

    private Runnable completionCallBack(SseKey sseKey) {
        return () -> removeEmitter(sseKey);
    }

    private Runnable timeoutCallBack(SseKey sseKey) {
        return () -> removeEmitter(sseKey);
    }

    private Consumer<Throwable> errorCallBack(SseKey sseKey) {
        return throwable -> removeEmitter(sseKey);
    }
}
