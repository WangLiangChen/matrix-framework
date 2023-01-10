package wang.liangchen.matrix.framework.web.response;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.util.WebUtils;

import java.io.*;

/**
 * @author LiangChen.Wang
 * 弥补body只能读取一次的不足
 */
@SuppressWarnings("NullableProblems")
public final class HttpServletResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {
    /**
     * 所有针对Response的ServletOutputStream的操作,都会转移到这
     */
    private final FastByteArrayOutputStream stealOutputStream = new FastByteArrayOutputStream(1024);

    private final ServletOutputStream servletOutputStreamWrapper;
    private final PrintWriter printWriterWrapper;
    private int statusCode = HttpServletResponse.SC_OK;
    private Integer contentLength;
    private boolean asyncRequestStarted;

    public HttpServletResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        ServletOutputStream servletOutputStream = getResponse().getOutputStream();
        this.servletOutputStreamWrapper = new ResponseServletOutputStreamWrapper(servletOutputStream);
        String characterEncoding = getCharacterEncoding();
        this.printWriterWrapper = (characterEncoding != null ? new ResponsePrintWriterWrapper(characterEncoding) : new ResponsePrintWriterWrapper(WebUtils.DEFAULT_CHARACTER_ENCODING));
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return this.servletOutputStreamWrapper;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return this.printWriterWrapper;
    }


    @Override
    public void setStatus(int sc) {
        this.statusCode = sc;
        super.setStatus(sc);
    }

    @Override
    public void sendError(int sc) {
        this.statusCode = sc;
        super.setStatus(SC_OK);
        //copyBodyToResponse(false);
    }

    @Override
    public void sendError(int sc, String msg) {
        this.statusCode = sc;
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
        if (len > this.stealOutputStream.size()) {
            this.stealOutputStream.resize(len);
        }
        this.contentLength = len;
    }

    // Overrides Servlet 3.1 setContentLengthLong(long) at runtime
    public void setContentLengthLong(long len) {
        if (len > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Content-Length exceeds ContentCachingResponseWrapper's maximum (" + Integer.MAX_VALUE + "): " + len);
        }
        int lenInt = (int) len;
        if (lenInt > this.stealOutputStream.size()) {
            this.stealOutputStream.resize(lenInt);
        }
        this.contentLength = lenInt;
    }

    @Override
    public void setBufferSize(int size) {
        if (size > this.stealOutputStream.size()) {
            this.stealOutputStream.resize(size);
        }
    }

    @Override
    public void resetBuffer() {
        this.stealOutputStream.reset();
    }

    @Override
    public void reset() {
        super.reset();
        this.stealOutputStream.reset();
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
        return this.stealOutputStream.toByteArray();
    }

    /**
     * Return an {@link InputStream} to the cached content.
     *
     * @return inputStream
     * @since 4.2
     */
    public InputStream getContentInputStream() {
        return this.stealOutputStream.getInputStream();
    }

    /**
     * Return the current size of the cached content.
     *
     * @return int
     * @since 4.2
     */
    public int getContentSize() {
        return this.stealOutputStream.size();
    }

    /**
     * Copy the cached body content to the response.
     *
     * @param complete whether to set a corresponding content length for the complete cached body content
     * @throws IOException
     */
    public void copyBodyToResponse(boolean complete) throws IOException {
        if (this.stealOutputStream.size() <= 0) {
            return;
        }

        HttpServletResponse response = (HttpServletResponse) getResponse();
        if ((complete || this.contentLength != null) && !response.isCommitted()) {
            response.setContentLength(complete ? this.stealOutputStream.size() : this.contentLength);
            this.contentLength = null;
        }
        this.stealOutputStream.writeTo(response.getOutputStream());
        this.stealOutputStream.reset();
        if (complete) {
            super.flushBuffer();
        }
    }

    public void setAsyncRequestStarted(boolean asyncRequestStarted) {
        this.asyncRequestStarted = asyncRequestStarted;
    }

    public boolean isAsyncRequestStarted() {
        return asyncRequestStarted;
    }

    private class ResponseServletOutputStreamWrapper extends ServletOutputStream {

        private final ServletOutputStream servletOutputStream;

        public ResponseServletOutputStreamWrapper(ServletOutputStream servletOutputStream) {
            this.servletOutputStream = servletOutputStream;
        }

        @Override
        public void write(int b) throws IOException {
            stealOutputStream.write(b);
            if (isAsyncRequestStarted()) {
                this.servletOutputStream.write(b);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            stealOutputStream.write(b, off, len);
            if (isAsyncRequestStarted()) {
                this.servletOutputStream.write(b, off, len);
            }
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
            if (isAsyncRequestStarted()) {
                this.servletOutputStream.flush();
            }
        }
    }

    private class ResponsePrintWriterWrapper extends PrintWriter {

        public ResponsePrintWriterWrapper(String characterEncoding) throws UnsupportedEncodingException {
            super(new OutputStreamWriter(stealOutputStream, characterEncoding));
        }

        @Override
        public void write(char[] buf, int off, int len) {
            super.write(buf, off, len);
            super.flush();
        }

        @Override
        public void write(String s, int off, int len) {
            super.write(s, off, len);
            super.flush();
        }

        @Override
        public void write(int c) {
            super.write(c);
            super.flush();
        }
    }
}
