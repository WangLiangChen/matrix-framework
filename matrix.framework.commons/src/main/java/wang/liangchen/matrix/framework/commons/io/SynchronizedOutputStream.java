package wang.liangchen.matrix.framework.commons.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Liangchen.Wang 2021-11-02 17:14
 */
public class SynchronizedOutputStream extends OutputStream {
    private final OutputStream out;
    private final Object lock;

    SynchronizedOutputStream(OutputStream out) {
        this(out, out);
    }

    SynchronizedOutputStream(OutputStream out, Object lock) {
        this.out = out;
        this.lock = lock;
    }

    @Override
    public void write(int datum) throws IOException {
        synchronized (lock) {
            out.write(datum);
        }
    }

    @Override
    public void write(byte[] data) throws IOException {
        synchronized (lock) {
            out.write(data);
        }
    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        synchronized (lock) {
            out.write(data, offset, length);
        }
    }

    @Override
    public void flush() throws IOException {
        synchronized (lock) {
            out.flush();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            out.close();
        }
    }
}