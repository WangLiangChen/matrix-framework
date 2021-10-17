package liangchen.wang.matrix.framework.web.exchange;

import liangchen.wang.matrix.framework.web.request.ServerHttpRequestDecorator;
import liangchen.wang.matrix.framework.web.response.ServerHttpResponseDecorator;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
/**
 * @author Liangchen.Wang
 * 用于弥补body只能读取一次的不足
 */
@SuppressWarnings("NullableProblems")
public final class ServerWebExchangeDecorator extends org.springframework.web.server.ServerWebExchangeDecorator {
    private final ServerHttpRequestDecorator requestDecorator;
    private final ServerHttpResponseDecorator responseDecorator;

    public ServerWebExchangeDecorator(ServerWebExchange delegate) {
        super(delegate);
        requestDecorator = new ServerHttpRequestDecorator(delegate.getRequest());
        responseDecorator = new ServerHttpResponseDecorator(delegate.getResponse());
    }

    @Override
    public ServerHttpRequest getRequest() {
        return requestDecorator;
    }

    @Override
    public ServerHttpResponse getResponse() {
        return responseDecorator;
    }
}
