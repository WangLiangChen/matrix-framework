package wang.liangchen.matrix.framework.web.response;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public final class HttpServletResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {
    private CachedServletOutputStream outputStream;
    private PrintWriter writer;
    private Integer contentLength;

    public HttpServletResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (null == this.outputStream) {
            this.outputStream = new CachedServletOutputStream(getResponse());
        }
        return this.outputStream;
    }

    @Override
    public String getCharacterEncoding() {
        String encoding = super.getCharacterEncoding();
        return (encoding == null ? WebUtils.DEFAULT_CHARACTER_ENCODING : encoding);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (null == this.writer) {
            this.writer = new PrintWriter(new OutputStreamWriter(this.outputStream, getCharacterEncoding()));
        }
        return this.writer;
    }

    @Override
    public void sendError(int statusCode) {
        super.setStatus(SC_OK);
        //copyBodyToResponse(false);
    }

    @Override
    public void sendError(int statusCode, String msg) {
        super.setStatus(SC_OK);
        //copyBodyToResponse(false);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        copyBodyToResponse(false);
        super.sendRedirect(location);
    }


    @Override
    public void flushBuffer() {
        // do not flush the underlying response as the content as not been copied to it yet
    }

    @Override
    public void setContentLength(int len) {
        if (len > this.outputStream.getCachedOutputStream().size()) {
            this.outputStream.getCachedOutputStream().resize(len);
        }
        this.contentLength = len;
    }

    public void setContentLengthLong(long len) {
        setContentLength((int) len);
    }

    @Override
    public void setBufferSize(int size) {
        if (size > this.outputStream.getCachedOutputStream().size()) {
            this.outputStream.getCachedOutputStream().resize(size);
        }
    }

    @Override
    public void resetBuffer() {
        super.resetBuffer();
        this.outputStream.reset();
    }

    @Override
    public void reset() {
        super.reset();
        this.outputStream.reset();
    }

    public byte[] getByteArray() {
        return this.outputStream.getCachedOutputStream().toByteArray();
    }

    public int getSize() {
        return this.outputStream.size();
    }

    /**
     * Copy the cached body content to the response.
     *
     * @param complete whether to set a corresponding content length for the complete cached body content
     * @throws IOException IOException
     */
    public void copyBodyToResponse(boolean complete) throws IOException {
        if (this.outputStream.getCachedOutputStream().size() <= 0) {
            return;
        }

        HttpServletResponse response = (HttpServletResponse) getResponse();
        if ((complete || this.contentLength != null) && !response.isCommitted()) {
            this.contentLength = complete ? this.outputStream.size() : this.contentLength;
            response.setContentLength(this.contentLength);
        }
        this.outputStream.getCachedOutputStream().writeTo(response.getOutputStream());
        this.outputStream.reset();
        if (complete) {
            super.flushBuffer();
        }
    }

    private static class CachedServletOutputStream extends ServletOutputStream {

        private final ServletOutputStream servletOutputStream;
        private final FastByteArrayOutputStream cachedOutputStream;

        public CachedServletOutputStream(ServletResponse response) throws IOException {
            this.servletOutputStream = response.getOutputStream();
            this.cachedOutputStream = new FastByteArrayOutputStream(1024);
        }

        @Override
        public void write(int b) throws IOException {
            cachedOutputStream.write(b);
            servletOutputStream.write(b);
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            cachedOutputStream.write(bytes, off, len);
            servletOutputStream.write(bytes, off, len);
        }

        @Override
        public void write(byte[] b) throws IOException {
            cachedOutputStream.write(b);
            servletOutputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return this.servletOutputStream.isReady();
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            this.servletOutputStream.setWriteListener(writeListener);
        }

        @Override
        public void flush() throws IOException {
            servletOutputStream.flush();
        }

        public FastByteArrayOutputStream getCachedOutputStream() {
            return cachedOutputStream;
        }

        public void reset() {
            this.cachedOutputStream.reset();
        }

        public int size() {
            return this.cachedOutputStream.size();
        }
    }

}