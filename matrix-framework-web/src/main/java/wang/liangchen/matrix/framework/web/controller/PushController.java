package wang.liangchen.matrix.framework.web.controller;


import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import wang.liangchen.matrix.framework.web.push.PushUtil;
import wang.liangchen.matrix.framework.web.push.PusherKey;
import wang.liangchen.matrix.framework.web.push.PusherType;
import wang.liangchen.matrix.framework.web.response.FormattedResponse;

import java.util.Map;

/**
 * Server Side Event
 *
 * @author LiangChen.Wang 2019/9/28 15:06
 * 异步异常拦截 CallableProcessingInterceptor/DeferredResultProcessingInterceptor/AsyncHandlerInterceptor
 */
@RestController
@RequestMapping("/push")
public class PushController {
    @GetMapping("/deferredResult")
    @PostMapping("/deferredResult")
    public DeferredResult<FormattedResponse<?>> deferredResult(@RequestParam Map<String, String> queryParams, @Nullable @RequestBody String body) {
        return PushUtil.INSTANCE.appendPusher(PusherType.DeferredResult, PusherKey.newInstance(queryParams, body));
    }

    @GetMapping("/sse")
    @PostMapping("/sse")
    public SseEmitter sse(Map<String, String> queryParams, @RequestBody String body) {
        return PushUtil.INSTANCE.appendPusher(PusherType.DeferredResult, PusherKey.newInstance(queryParams, body));
    }

    @GetMapping("/streamingResponseBody")
    @PostMapping("/streamingResponseBody")
    public StreamingResponseBody streamingResponseBody(Map<String, String> queryParams, @RequestBody String body) {
        // 用于直接将结果写出到Response的OutputStream中； 如文件下载等
        return outputStream -> {
        };
    }
}
