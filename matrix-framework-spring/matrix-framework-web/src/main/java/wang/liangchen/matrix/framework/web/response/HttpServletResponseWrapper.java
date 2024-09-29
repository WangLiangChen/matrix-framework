package wang.liangchen.matrix.framework.web.response;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public final class HttpServletResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {
    private CachedServletOutputStream outputStream;
    private PrintWriter writer;
    private int statusCode = HttpServletResponse.SC_OK;
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
    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
        super.setStatus(statusCode);
    }

    @Override
    public void sendError(int statusCode) {
        this.statusCode = statusCode;
        super.setStatus(SC_OK);
        //copyBodyToResponse(false);
    }

    @Override
    public void sendError(int statusCode, String msg) {
        this.statusCode = statusCode;
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
        this.outputStream.getCachedOutputStream().reset();
    }

    @Override
    public void reset() {
        super.reset();
        this.outputStream.getCachedOutputStream().reset();
    }

    /**
     * Return the status code as specified on the response.
     *
     * @return int
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * Return the cached response content as a byte array.
     *
     * @return byte[]
     */
    public byte[] getContentAsByteArray() {
        return this.outputStream.getCachedOutputStream().toByteArray();
    }

    /**
     * Return an {@link InputStream} to the cached content.
     *
     * @return inputStream
     * @since 4.2
     */
    public InputStream getContentInputStream() {
        return this.outputStream.getCachedOutputStream().getInputStream();
    }

    /**
     * Return the current size of the cached content.
     *
     * @return int
     * @since 4.2
     */
    public int getContentSize() {
        return this.outputStream.getCachedOutputStream().size();
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
            response.setContentLength(complete ? this.outputStream.getCachedOutputStream().size() : this.contentLength);
            this.contentLength = null;
        }
        this.outputStream.getCachedOutputStream().writeTo(response.getOutputStream());
        this.outputStream.getCachedOutputStream().reset();
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
    }

}