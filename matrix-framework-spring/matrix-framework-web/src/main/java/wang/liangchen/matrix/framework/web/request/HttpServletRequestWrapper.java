package wang.liangchen.matrix.framework.web.request;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.WebUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author LiangChen.Wang
 * 弥补body只能读取一次的缺陷
 */
public final class HttpServletRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private CachedServletInputStream inputStream;
    private BufferedReader reader;


    public HttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (null == this.inputStream) {
            return new CachedServletInputStream(this.getRequest());
        }
        return this.inputStream;
    }

    @Override
    public String getCharacterEncoding() {
        String encoding = super.getCharacterEncoding();
        return (encoding == null ? WebUtils.DEFAULT_CHARACTER_ENCODING : encoding);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (this.reader == null) {
            this.reader = new BufferedReader(new InputStreamReader(this.inputStream, getCharacterEncoding()));
        }
        return this.reader;
    }

    private boolean isFormPost() {
        String contentType = getContentType();
        return (contentType != null && contentType.contains(FORM_CONTENT_TYPE) && HttpMethod.POST.matches(getMethod()));
    }

    public byte[] getByteArray() {
        return this.inputStream.getCachedOutputStream().toByteArray();
    }


    private static class CachedServletInputStream extends ServletInputStream {
        private final ServletInputStream servletInputStream;
        private final ByteArrayOutputStream cachedOutputStream;

        public CachedServletInputStream(ServletRequest request) throws IOException {
            this.servletInputStream = request.getInputStream();
            this.cachedOutputStream = new ByteArrayOutputStream(request.getContentLength());
        }

        @Override
        public int read() throws IOException {
            int nextByte = this.servletInputStream.read();
            if (-1 == nextByte) {
                return nextByte;
            }
            this.cachedOutputStream.write(nextByte);
            return nextByte;
        }

        @Override
        public boolean isFinished() {
            return this.servletInputStream.isFinished();
        }

        @Override
        public boolean isReady() {
            return this.servletInputStream.isReady();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            this.servletInputStream.setReadListener(readListener);
        }

        public ByteArrayOutputStream getCachedOutputStream() {
            return cachedOutputStream;
        }
    }

}