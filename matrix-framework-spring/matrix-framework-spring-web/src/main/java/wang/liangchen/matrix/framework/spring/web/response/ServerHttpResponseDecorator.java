package wang.liangchen.matrix.framework.spring.web.response;

import org.springframework.http.server.reactive.ServerHttpResponse;


/**
 * @author Liangchen.Wang
 * 用于弥补body只能读取一次的不足
 */

public final class ServerHttpResponseDecorator extends org.springframework.http.server.reactive.ServerHttpResponseDecorator {

    public ServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }
}