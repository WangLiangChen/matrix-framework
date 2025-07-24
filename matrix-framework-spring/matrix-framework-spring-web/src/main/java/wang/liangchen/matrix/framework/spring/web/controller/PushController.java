package wang.liangchen.matrix.framework.spring.web.controller;


import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import wang.liangchen.matrix.framework.spring.web.response.JsonResponse;
import wang.liangchen.matrix.framework.spring.web.utils.PushUtil;
import wang.liangchen.matrix.framework.spring.web.webpush.PusherKey;
import wang.liangchen.matrix.framework.spring.web.webpush.PusherType;

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

    @RequestMapping(value = "deferredResult", method = {RequestMethod.GET, RequestMethod.POST})
    public DeferredResult<JsonResponse<?>> deferredResult(@RequestParam Map<String, String> queryParams, @Nullable @RequestBody String body) {
        return PushUtil.INSTANCE.appendPusher(PusherType.DeferredResult, PusherKey.newInstance(queryParams, body));
    }


    @RequestMapping(value = "sse", method = {RequestMethod.GET, RequestMethod.POST})
    public SseEmitter sse(Map<String, String> queryParams, @RequestBody String body) {
        return PushUtil.INSTANCE.appendPusher(PusherType.DeferredResult, PusherKey.newInstance(queryParams, body));
    }


    @RequestMapping(value = "streamingResponseBody", method = {RequestMethod.GET, RequestMethod.POST})
    public StreamingResponseBody streamingResponseBody(Map<String, String> queryParams, @RequestBody String body) {
        // 用于直接将结果写出到Response的OutputStream中； 如文件下载等
        return outputStream -> {
        };
    }
}
