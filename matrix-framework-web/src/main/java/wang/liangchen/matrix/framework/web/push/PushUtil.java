package wang.liangchen.matrix.framework.web.push;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author LiangChen.Wang
 */
public enum PushUtil {
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
    private final Map<PusherKey, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>(64);
    private final Map<PusherKey, DeferredResult<String>> deferredResultMap = new ConcurrentHashMap<>(64);


    @SuppressWarnings("unchecked")
    public <T> T appendPusher(PusherKey pusherKey, Class<T> pusherClass) {
        if (pusherClass.isAssignableFrom(SseEmitter.class)) {
            return (T) appendSseEmitter(pusherKey);
        }
        if (pusherClass.isAssignableFrom(DeferredResult.class)) {
            return (T) appendDeferredResult(pusherKey);
        }
        throw new MatrixInfoException("Unsupported type:{}", pusherClass);
    }

    private SseEmitter appendSseEmitter(PusherKey pusherKey) {
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter();
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(pusherKey));
        sseEmitter.onError(errorCallBack(pusherKey));
        sseEmitter.onTimeout(timeoutCallBack(pusherKey));
        sseEmitterMap.put(pusherKey, sseEmitter);
        // 数量+1
        count.getAndIncrement();
        return sseEmitter;
    }

    private DeferredResult<?> appendDeferredResult(PusherKey pusherKey) {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        deferredResult.onCompletion(completionCallBack(pusherKey));
        deferredResult.onError(errorCallBack(pusherKey));
        deferredResult.onTimeout(timeoutCallBack(pusherKey));
        deferredResultMap.put(pusherKey, deferredResult);
        count.getAndIncrement();
        return deferredResult;
    }


    public void unicast(PusherKey pusherKey, String message) {
        SseEmitter sseEmitter = sseEmitterMap.get(pusherKey);
        DeferredResult<String> deferredResult = deferredResultMap.get(pusherKey);
        try {
            if (null != sseEmitter) {
                sseEmitter.send(message);
            }
            if (null != deferredResult) {
                deferredResult.setResult(message);
            }
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }

    public void multicast(String group, String message) {
        Set<PusherKey> pusherKeys = sseEmitterMap.keySet();
        pusherKeys.stream().filter(e -> e.getGroup().equals(group)).forEach(e -> unicast(e, message));
    }

    public void broadcast(String message) {
        Set<PusherKey> pusherKeys = sseEmitterMap.keySet();
        pusherKeys.forEach(e -> unicast(e, message));
    }

    public void removePusher(PusherKey pusherKey) {
        sseEmitterMap.remove(pusherKey);
        deferredResultMap.remove(pusherKey);
        count.getAndDecrement();
    }


    private Runnable completionCallBack(PusherKey pusherKey) {
        return () -> removePusher(pusherKey);
    }

    private Runnable timeoutCallBack(PusherKey pusherKey) {
        return () -> removePusher(pusherKey);
    }

    private Consumer<Throwable> errorCallBack(PusherKey pusherKey) {
        return throwable -> removePusher(pusherKey);
    }

}
