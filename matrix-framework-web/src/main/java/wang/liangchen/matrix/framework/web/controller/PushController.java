package wang.liangchen.matrix.framework.web.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import wang.liangchen.matrix.framework.web.push.PushUtil;
import wang.liangchen.matrix.framework.web.push.PusherKey;

/**
 * Server Side Event
 *
 * @author LiangChen.Wang 2019/9/28 15:06
 * 异步异常拦截 CallableProcessingInterceptor/DeferredResultProcessingInterceptor/AsyncHandlerInterceptor
 */
@RestController
@RequestMapping("push")
public class PushController {

    @GetMapping("/")
    public SseEmitter sse(String name, String group) {
        return PushUtil.INSTANCE.appendPusher(PusherKey.newInstance(name, group), SseEmitter.class);
    }

    @GetMapping("/deferredResult")
    public DeferredResult<?> deferredResult(String name, String group) {
        return PushUtil.INSTANCE.appendPusher(PusherKey.newInstance(name, group), DeferredResult.class);
    }

    @GetMapping("/streamingResponseBody")
    public StreamingResponseBody streamingResponseBody() {
        // 用于直接将结果写出到Response的OutputStream中； 如文件下载等
        return outputStream -> {
        };
    }
}
