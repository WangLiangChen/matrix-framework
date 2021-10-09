package liangchen.wang.matrix.framework.web.response;

import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.List;

public class ResponseBodyResultHandler extends org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler {
    private final MethodParameter methodParameter;

    public ResponseBodyResultHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
        this(writers, resolver, ReactiveAdapterRegistry.getSharedInstance());
    }

    public ResponseBodyResultHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver, ReactiveAdapterRegistry registry) {
        super(writers, resolver, registry);
        // 通过本类的一个方法构造MethodParameter
        try {
            Method method = this.getClass().getDeclaredMethod("responseMethod");
            //  -1 for the method return type;
            methodParameter = new MethodParameter(method, -1);
        } catch (NoSuchMethodException e) {
            throw new MatrixErrorException(e);
        }
    }

    @Override
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        Object returnValue = result.getReturnValue();
        if (returnValue instanceof Mono) {
            Mono<?> monoBody = ((Mono<?>) returnValue).map(this::wrapBody);
            return writeBody(monoBody, methodParameter, exchange);
        }
        if (returnValue instanceof Flux) {
            Mono<?> monoBody = ((Flux<?>) returnValue).collectList().map(this::wrapBody);
            return writeBody(monoBody, methodParameter, exchange);
        }
        Mono<?> monoBody = Mono.just(wrapBody(returnValue));
        return writeBody(monoBody, methodParameter, exchange);
    }

    private static Mono<FormattedResponse> responseMethod() {
        return Mono.empty();
    }

    private FormattedResponse<?> wrapBody(Object body) {
        if (body instanceof FormattedResponse) {
            return (FormattedResponse<?>) body;
        }
        return FormattedResponse.success().payload(body);
    }
}
