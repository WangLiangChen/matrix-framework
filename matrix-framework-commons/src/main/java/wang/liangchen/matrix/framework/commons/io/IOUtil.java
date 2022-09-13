package wang.liangchen.matrix.framework.commons.io;


import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author LiangChen.Wang 2019/8/18 15:46
 */
public enum IOUtil {
    /**
     * instance
     */
    INSTANCE;
    private final int DEFAULT_BUFFER_SIZE = 8192;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public <T> void slice(Stream<T> stream, int sliceSize, Consumer<List<T>> consumer) {
        AtomicInteger atomicIndex = new AtomicInteger();
        @SuppressWarnings("unchecked")
        ArrayList<T>[] container = new ArrayList[1];
        stream.forEach(e -> {
            int index = atomicIndex.getAndIncrement();
            if (0 == index) {
                container[0] = new ArrayList<>();
            }
            container[0].add(e);
            if (index + 2 > sliceSize) {
                atomicIndex.set(0);
                consumer.accept(container[0]);
            }
        });
        //输出剩余数据
        if (container[0].size() < sliceSize) {
            consumer.accept(container[0]);
        }
    }

    public String read(InputStream inputStream, String encoding, int bufferSize) {
        Assert.INSTANCE.notNull(inputStream, "inputStream must not be null");
        if (bufferSize <= 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }
        if (StringUtil.INSTANCE.isBlank(encoding)) {
            encoding = DEFAULT_CHARSET.name();
        }
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream stream = inputStream) {
            int length;
            while ((length = stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toString(encoding);
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }

    public String read(InputStream inputStream, String encoding) {
        return read(inputStream, encoding, 0);
    }

    public String read(InputStream inputStream) {
        return read(inputStream, null, 0);
    }

    public String read(InputStream inputStream, int bufferSize) {
        return read(inputStream, null, bufferSize);
    }

    public void io(InputStream inputStream, OutputStream outputStream, int bufferSize) {
        Assert.INSTANCE.notNull(inputStream, "inputStream must not be null");
        Assert.INSTANCE.notNull(outputStream, "outputStream must not be null");
        if (bufferSize <= 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }
        byte[] buffer = new byte[bufferSize];
        try (InputStream stream = inputStream) {
            int length;
            while ((length = stream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }

    public void io(InputStream in, OutputStream out) {
        io(in, out, -1);
    }

    public void io(Reader in, Writer out) {
        io(in, out, -1);
    }

    public void io(Reader reader, Writer writer, int bufferSize) {
        Assert.INSTANCE.notNull(reader, "reader must not be null");
        Assert.INSTANCE.notNull(writer, "writer must not be null");
        if (bufferSize <= 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }
        char[] buffer = new char[bufferSize];
        try (Reader r = reader) {
            int length;
            while ((length = r.read(buffer)) >= 0) {
                writer.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }
}
