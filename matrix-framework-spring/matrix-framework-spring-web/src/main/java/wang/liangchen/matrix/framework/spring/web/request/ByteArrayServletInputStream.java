package wang.liangchen.matrix.framework.spring.web.request;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

class ByteArrayServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream inputStream;

    ByteArrayServletInputStream(byte[] bytes) throws IOException {
        this.inputStream = new ByteArrayInputStream(bytes);
    }

    @Override
    public int read() throws IOException {
        return this.inputStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return super.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
}
