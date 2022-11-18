package wang.liangchen.matrix.framework.web.push;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.web.response.FormattedResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author LiangChen.Wang
 */
public enum PushUtil {
    /**
     * instance
     */
    INSTANCE;
    /**
     * 连接数
     */
    private final AtomicInteger count = new AtomicInteger(0);
    private final Map<PusherKey, SseEmitter> sseEmitterContainer = new ConcurrentHashMap<>(64);
    private final Map<PusherKey, DeferredResult<FormattedResponse<?>>> deferredResultContainer = new ConcurrentHashMap<>(64);


    public Set<PusherKey> filterPusherKeys(PusherType pusherType, Predicate<PusherKey> predicate) {
        switch (pusherType) {
            case DeferredResult:
                return deferredResultContainer.keySet().stream().filter(predicate).collect(Collectors.toSet());
            case SseEmitter:
                return sseEmitterContainer.keySet().stream().filter(predicate).collect(Collectors.toSet());
            default:
                throw new MatrixWarnException("Unsupported type");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T appendPusher(PusherType pusherType, PusherKey pusherKey) {
        switch (pusherType) {
            case DeferredResult:
                return (T) appendDeferredResult(pusherKey);
            case SseEmitter:
                return (T) appendSseEmitter(pusherKey);
            default:
                throw new MatrixWarnException("Unsupported type");
        }
    }

    public void unicast(PusherType pusherType, PusherKey pusherKey, Object message) {
        switch (pusherType) {
            case DeferredResult:
                DeferredResult<FormattedResponse<?>> deferredResult = deferredResultContainer.get(pusherKey);
                if (null != deferredResult) {
                    deferredResult.setResult(FormattedResponse.success().payload(message));
                }
                break;
            case SseEmitter:
                SseEmitter sseEmitter = sseEmitterContainer.get(pusherKey);
                if (null != sseEmitter) {
                    try {
                        sseEmitter.send(FormattedResponse.success().payload(message));
                    } catch (IOException e) {
                        throw new MatrixErrorException(e);
                    }
                }
                break;
            default:
                throw new MatrixWarnException("Unsupported type");
        }
    }

    public void multicast(PusherType pusherType, String group, Object message) {
        Set<PusherKey> pusherKeys;
        switch (pusherType) {
            case DeferredResult:
                pusherKeys = deferredResultContainer.keySet();
                break;
            case SseEmitter:
                pusherKeys = sseEmitterContainer.keySet();
                break;
            default:
                throw new MatrixWarnException("Unsupported type");
        }
        pusherKeys.stream().filter(pusherKey -> pusherKey.getGroup().equals(group)).forEach(pusherKey -> unicast(pusherType, pusherKey, message));
    }

    public void broadcast(PusherType pusherType, Object message) {
        Set<PusherKey> pusherKeys;
        switch (pusherType) {
            case DeferredResult:
                pusherKeys = deferredResultContainer.keySet();
                break;
            case SseEmitter:
                pusherKeys = sseEmitterContainer.keySet();
                break;
            default:
                throw new MatrixWarnException("Unsupported type");
        }
        pusherKeys.forEach(pusherKey -> unicast(pusherType, pusherKey, message));
    }


    private DeferredResult<FormattedResponse<?>> appendDeferredResult(PusherKey pusherKey) {
        DeferredResult<FormattedResponse<?>> deferredResult = new DeferredResult<>();
        // 注册回调
        deferredResult.onCompletion(completionCallBack(PusherType.DeferredResult, pusherKey));
        deferredResult.onError(errorCallBack(PusherType.DeferredResult, pusherKey));
        deferredResult.onTimeout(timeoutCallBack(PusherType.DeferredResult, pusherKey));
        deferredResultContainer.put(pusherKey, deferredResult);
        // 数量+1
        count.getAndIncrement();
        return deferredResult;
    }

    private SseEmitter appendSseEmitter(PusherKey pusherKey) {
        SseEmitter sseEmitter = new SseEmitter();
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(PusherType.SseEmitter, pusherKey));
        sseEmitter.onError(errorCallBack(PusherType.SseEmitter, pusherKey));
        sseEmitter.onTimeout(timeoutCallBack(PusherType.SseEmitter, pusherKey));
        sseEmitterContainer.put(pusherKey, sseEmitter);
        // 数量+1
        count.getAndIncrement();
        return sseEmitter;
    }


    private Runnable completionCallBack(PusherType pusherType, PusherKey pusherKey) {
        return () -> removePusher(pusherType, pusherKey, RemoveCause.COMPLETION);
    }

    private Runnable timeoutCallBack(PusherType pusherType, PusherKey pusherKey) {
        return () -> {
            removePusher(pusherType, pusherKey, RemoveCause.TIMEOUT);
        };
    }

    private Consumer<Throwable> errorCallBack(PusherType pusherType, PusherKey pusherKey) {
        return throwable -> removePusher(pusherType, pusherKey, RemoveCause.ERROR);
    }

    private void removePusher(PusherType pusherType, PusherKey pusherKey, RemoveCause removeCause) {
        switch (pusherType) {
            case DeferredResult:
                removeDeferredResult(pusherKey, removeCause);
                break;
            case SseEmitter:
                removeSseEmitter(pusherKey, removeCause);
                break;
            default:
                throw new MatrixWarnException("Unsupported type");
        }
    }

    private void removeDeferredResult(PusherKey pusherKey, RemoveCause removeCause) {
        DeferredResult<FormattedResponse<?>> deferredResult = deferredResultContainer.get(pusherKey);
        if (null == deferredResult) {
            return;
        }
        deferredResultContainer.remove(pusherKey);
        if (RemoveCause.COMPLETION != removeCause) {
            deferredResult.setResult(FormattedResponse.failure().code(removeCause.toString()));
        }
    }

    private void removeSseEmitter(PusherKey pusherKey, RemoveCause removeCause) {
        SseEmitter sseEmitter = sseEmitterContainer.get(pusherKey);
        if (null == sseEmitter) {
            return;
        }
        sseEmitterContainer.remove(pusherKey);
        if (RemoveCause.COMPLETION != removeCause) {
            try {
                sseEmitter.send(FormattedResponse.failure().code(removeCause.toString()));
            } catch (IOException e) {
                throw new MatrixErrorException(e);
            }
        }
    }

}
