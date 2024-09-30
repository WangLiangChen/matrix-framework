package wang.liangchen.matrix.framework.web.response;

import io.netty.buffer.UnpooledByteBufAllocator;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static reactor.core.scheduler.Schedulers.single;


/**
 * @author Liangchen.Wang
 * 用于弥补body只能读取一次的不足
 */

public final class ServerHttpResponseDecorator extends org.springframework.http.server.reactive.ServerHttpResponseDecorator {
    public ServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (body instanceof Mono) {
            //noinspection unchecked
            final Mono<DataBuffer> monoBody = (Mono<DataBuffer>) body;
            return super.writeWith(monoBody.publishOn(single()).map(this::copyDataBuffer));
        }
        if (body instanceof Flux) {
            //noinspection unchecked
            final Flux<DataBuffer> monoBody = (Flux<DataBuffer>) body;
            return super.writeWith(monoBody.publishOn(single()).map(this::copyDataBuffer));
        }
        return super.writeWith(body);
    }

    private DataBuffer copyDataBuffer(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
        return dataBufferFactory.wrap(bytes);
    }
}