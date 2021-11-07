package wang.liangchen.matrix.framework.web.response;

import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.InvocableHandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.web.annotation.FormattedResponseIgnore;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;
@SuppressWarnings("NullableProblems")
public final class ResponseBodyResultHandler extends org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler {
    private final MethodParameter monoParameter;

    public ResponseBodyResultHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
        this(writers, resolver, ReactiveAdapterRegistry.getSharedInstance());
    }

    public ResponseBodyResultHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver, ReactiveAdapterRegistry registry) {
        super(writers, resolver, registry);
        // 通过本类的一个方法构造MethodParameter
        try {
            Method method = this.getClass().getDeclaredMethod("monoResponse");
            //  -1 for the method return type;
            monoParameter = new MethodParameter(method, -1);
        } catch (NoSuchMethodException e) {
            throw new MatrixErrorException(e);
        }
    }

    @Override
    protected MediaType selectMediaType(ServerWebExchange exchange, Supplier<List<MediaType>> producibleTypesSupplier) {
        return super.selectMediaType(exchange, producibleTypesSupplier);
    }

    @Override
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        InvocableHandlerMethod handler = (InvocableHandlerMethod) result.getHandler();
        // 获取类上的注解
        FormattedResponseIgnore ignore = handler.getBeanType().getAnnotation(FormattedResponseIgnore.class);
        if (null != ignore) {
            return super.handleResult(exchange, result);
        }
        // 获取方法上的注解
        ignore = handler.getMethodAnnotation(FormattedResponseIgnore.class);
        if (null != ignore) {
            return super.handleResult(exchange, result);
        }

        Object returnValue = result.getReturnValue();
        if (returnValue instanceof Mono) {
            Mono<?> monoBody = ((Mono<?>) returnValue).map(this::wrapBody);
            return writeBody(monoBody, monoParameter, exchange);
        }
        if (returnValue instanceof Flux) {
            Mono<?> monoBodys = ((Flux<?>) returnValue).collectList().map(this::wrapBody);
            return writeBody(monoBodys, monoParameter, exchange);
        }
        Mono<?> monoBody = Mono.just(wrapBody(returnValue));
        return writeBody(monoBody, monoParameter, exchange);
    }

    private static Mono<FormattedResponse> monoResponse() {
        return Mono.empty();
    }

    private FormattedResponse wrapBody(Object body) {
        if (body instanceof FormattedResponse) {
            return (FormattedResponse) body;
        }
        return FormattedResponse.success().payload(body);
    }
}
