package wang.liangchen.matrix.framework.web.request;

import io.netty.buffer.UnpooledByteBufAllocator;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;

import static reactor.core.scheduler.Schedulers.single;

/**
 * @author Liangchen.Wang
 * 用于弥补body只能读取一次的不足
 */
public final class ServerHttpRequestDecorator extends org.springframework.http.server.reactive.ServerHttpRequestDecorator {
    private final Flux<DataBuffer> body;

    public ServerHttpRequestDecorator(ServerHttpRequest request) {
        super(request);
        Flux<DataBuffer> superBody = super.getBody();
        this.body = superBody.publishOn(single()).map(this::copyDataBuffer);
    }


    @Override
    public Flux<DataBuffer> getBody() {
        return this.body;
    }

    private DataBuffer copyDataBuffer(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
        return dataBufferFactory.wrap(bytes);
    }
}