package wang.liangchen.matrix.framework.web.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import wang.liangchen.matrix.framework.web.exchange.ServerWebExchangeDecorator;
import wang.liangchen.matrix.framework.web.response.FormattedResponse;
import wang.liangchen.matrix.framework.web.response.ResponseBodyResultHandler;

/**
 * @author LiangChen.Wang
 * 不能与@EnableWebMvc同时存在,The Java/XML config for Spring MVC and Spring WebFlux cannot both be enabled, e.g. via @EnableWebMvc and @EnableWebFlux, in the same application.
 */
//@EnableWebFlux
@AutoConfigureBefore(org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.class)
public class WebFluxAutoConfiguration implements WebFluxConfigurer {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler() {
        return (exchange, ex) -> {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            String dataString = FormattedResponse.exception(ex).toString();
            if (ex instanceof ResponseStatusException) {
                ResponseStatusException responseStatusException = (ResponseStatusException) ex;
                if (responseStatusException.getStatus() == HttpStatus.NOT_FOUND) {
                    dataString = FormattedResponse.failure().code(HttpStatus.NOT_FOUND.name()).message("Resource not found:{}", exchange.getRequest().getPath()).toString();
                }
            }
            DataBuffer dataBuffer = dataBufferFactory.wrap(dataString.getBytes());
            return response.writeWith(Mono.just(dataBuffer));
        };
    }

    @Bean
    public WebFilter webFilter() {
        return (exchange, chain) ->  chain.filter(new ServerWebExchangeDecorator(exchange));
    }

    public HandlerFilterFunction<?, ?> handlerFilterFunction() {
        return (request, handlerFunction) -> handlerFunction.handle(request);
    }

    @Bean
    public ResponseBodyResultHandler customizedResponseBodyResultHandler(ServerCodecConfigurer serverCodecConfigurer, RequestedContentTypeResolver requestedContentTypeResolver) {
        return new ResponseBodyResultHandler(serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
    }

    @Bean
    public Scheduler scheduler() {
        return Schedulers.boundedElastic();
    }
}
