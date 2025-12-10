package wang.liangchen.matrix.framework.spring.web.request;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

import java.io.*;
import java.util.Objects;

/**
 * @author LiangChen.Wang
 * 弥补body只能读取一次的缺陷
 */
public final class HttpServletRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {

    private ByteArrayServletInputStream inputStream;
    private byte[] cachedBytes;

    public HttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        cacheRequestBody();
    }

    private void cacheRequestBody() throws IOException {
        if (Objects.isNull(cachedBytes)) {
            try (InputStream is = super.getInputStream(); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] temp = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(temp)) != -1) {
                    buffer.write(temp, 0, bytesRead);
                }
                cachedBytes = buffer.toByteArray();
            }
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        if (Objects.isNull(inputStream)) {
            inputStream = new ByteArrayServletInputStream(cachedBytes);
        }
        return inputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), this.getCharacterEncoding()));
    }

    public byte[] getByteArray() {
        return cachedBytes;
    }

    private static class ByteArrayServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public ByteArrayServletInputStream(byte[] cachedBytes) {
            this.inputStream = new ByteArrayInputStream(cachedBytes);
        }

        @Override
        public int read() {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(jakarta.servlet.ReadListener readListener) {
            // No-op: ReadListener is not supported in this implementation
        }
    }
}