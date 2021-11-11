package wang.liangchen.matrix.framework.commons.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author LiangChen.Wang
 */
public final class ByteOutputStream extends OutputStream {
    private byte[] buf;
    private int count;

    public ByteOutputStream() {
        this(1024);
    }

    public ByteOutputStream(int size) {
        this.count = 0;
        this.buf = new byte[size];
    }

    public void write(InputStream inputStream) throws IOException {
        int cappcity;
        if (inputStream instanceof ByteArrayInputStream) {
            cappcity = inputStream.available();
            this.ensureCapacity(cappcity);
            this.count += inputStream.read(this.buf, this.count, cappcity);
        } else {
            while (true) {
                cappcity = this.buf.length - this.count;
                int sz = inputStream.read(this.buf, this.count, cappcity);
                if (sz < 0) {
                    return;
                }

                this.count += sz;
                if (cappcity == sz) {
                    this.ensureCapacity(this.count);
                }
            }
        }
    }

    @Override
    public void write(int b) {
        this.ensureCapacity(1);
        this.buf[this.count] = (byte) b;
        ++this.count;
    }

    private void ensureCapacity(int capacity) {
        int newCount = capacity + this.count;
        if (newCount > this.buf.length) {
            byte[] newBuf = new byte[Math.max(this.buf.length << 1, newCount)];
            System.arraycopy(this.buf, 0, newBuf, 0, this.count);
            this.buf = newBuf;
        }

    }

    @Override
    public void write(byte[] b, int off, int len) {
        this.ensureCapacity(len);
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count += len;
    }

    @Override
    public void write(byte[] b) {
        this.write(b, 0, b.length);
    }

    public void writeAsAscii(String s) {
        int len = s.length();
        this.ensureCapacity(len);
        int ptr = this.count;

        for (int i = 0; i < len; ++i) {
            this.buf[ptr++] = (byte) s.charAt(i);
        }

        this.count = ptr;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.buf, 0, this.count);
    }

    public void reset() {
        this.count = 0;
    }

    public byte[] toByteArray() {
        byte[] newBuf = new byte[this.count];
        System.arraycopy(this.buf, 0, newBuf, 0, this.count);
        return newBuf;
    }

    public int size() {
        return this.count;
    }

    public ByteInputStream inputStream() {
        return new ByteInputStream(this.buf, this.count);
    }

    @Override
    public String toString() {
        return new String(this.buf, 0, this.count);
    }


    @Override
    public void close() {
    }

    public byte[] getBytes() {
        return this.buf;
    }

    public int getCount() {
        return this.count;
    }
}