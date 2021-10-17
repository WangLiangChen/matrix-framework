package liangchen.wang.matrix.framework.web.request;

import io.netty.buffer.UnpooledByteBufAllocator;
import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import static reactor.core.scheduler.Schedulers.single;

/**
 * @author Liangchen.Wang
 * 用于弥补body只能读取一次的不足
 */
@SuppressWarnings("NullableProblems")
public final class ServerHttpRequestDecorator extends org.springframework.http.server.reactive.ServerHttpRequestDecorator {
    private final Flux<DataBuffer> body;

    public ServerHttpRequestDecorator(ServerHttpRequest delegate) {
        super(delegate);
        Flux<DataBuffer> superBody = super.getBody();
        this.body = superBody.publishOn(single()).map(dataBuffer -> copyDataBuffer(dataBuffer, bytes -> {
        }));
    }


    @Override
    public Flux<DataBuffer> getBody() {
        return this.body;
    }

    private DataBuffer copyDataBuffer(DataBuffer dataBuffer, Consumer<byte[]> consumer) {
        try (InputStream in = dataBuffer.asInputStream()) {
            byte[] bytes = IOUtils.toByteArray(in);
            consumer.accept(bytes);
            DataBufferUtils.release(dataBuffer);
            DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
            return dataBufferFactory.wrap(bytes);
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }
}
