package liangchen.wang.matrix.framework.web.response;

import io.netty.buffer.UnpooledByteBufAllocator;
import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;
import org.apache.commons.io.IOUtils;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import static reactor.core.scheduler.Schedulers.single;

/**
 * @author Liangchen.Wang
 * 用于弥补body只能读取一次的不足
 */
public class ServerHttpResponseDecorator extends org.springframework.http.server.reactive.ServerHttpResponseDecorator {
    public ServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (body instanceof Mono) {
            final Mono<DataBuffer> monoBody = (Mono<DataBuffer>) body;
            return super.writeWith(monoBody.publishOn(single()).map(dataBuffer -> copyDataBuffer(dataBuffer, bytes -> {
            })));
        }
        if (body instanceof Flux) {
            final Flux<DataBuffer> monoBody = (Flux<DataBuffer>) body;
            return super.writeWith(monoBody.publishOn(single()).map(dataBuffer -> copyDataBuffer(dataBuffer, bytes -> {
            })));
        }
        return super.writeWith(body);
    }

    private <T extends DataBuffer> T copyDataBuffer(T dataBuffer, Consumer<byte[]> consumer) {
        try (InputStream in = dataBuffer.asInputStream()) {
            byte[] bytes = IOUtils.toByteArray(in);
            consumer.accept(bytes);
            DataBufferUtils.release(dataBuffer);
            DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
            return (T) dataBufferFactory.wrap(bytes);
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }
}
