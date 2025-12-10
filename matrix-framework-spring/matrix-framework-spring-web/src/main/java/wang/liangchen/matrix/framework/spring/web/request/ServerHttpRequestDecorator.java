package wang.liangchen.matrix.framework.spring.web.request;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author Liangchen.Wang
 * 用于弥补body只能读取一次的不足
 */
public final class ServerHttpRequestDecorator extends org.springframework.http.server.reactive.ServerHttpRequestDecorator {

    // DataBufferUtils.cache()
    public ServerHttpRequestDecorator(ServerHttpRequest delegate) {
        super(delegate);
    }
}