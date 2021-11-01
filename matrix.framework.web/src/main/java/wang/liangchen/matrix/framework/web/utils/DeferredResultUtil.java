package wang.liangchen.matrix.framework.web.utils;


import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.web.response.DeferredResultResponse;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public enum DeferredResultUtil {
    //
    INSTANCE;
    private final Map<String, Consumer<DeferredResultResponse>> map = new ConcurrentHashMap<>();

    public void putDeferredResult(String key, DeferredResult<DeferredResultResponse> deferredResult) {
        deferredResult.onTimeout(() -> {
            map.remove(key);
            DeferredResultResponse deferredResultResponse = new DeferredResultResponse();
            deferredResult.setResult(deferredResultResponse);
        });
        Optional.of(map).filter(e -> !e.containsKey(key)).orElseThrow(() -> new MatrixErrorException("DeferredResult is existed,key:" + key));
        // put Consumer的实现，accept时 相当于调用setResult
        map.putIfAbsent(key, deferredResult::setResult);
    }

    public void setResult(String key, DeferredResultResponse deferredResultResponse) {
        if (map.containsKey(key)) {
            Consumer<DeferredResultResponse> deferredResultResponseConsumer = map.get(key);
            // accept时 相当于调用setResult
            deferredResultResponseConsumer.accept(deferredResultResponse);
            map.remove(key);
        }
    }
}
