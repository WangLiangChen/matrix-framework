package liangchen.wang.matrix.framework.web.request;

import org.springframework.http.HttpMethod;
import org.springframework.web.util.WebUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author LiangChen.Wang
 * 弥补body只能读取一次的缺陷
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final ByteArrayOutputStream cachedContent;

    private final Integer contentCacheLimit;

    private ServletInputStream inputStream;

    private BufferedReader reader;

    /**
     * Create a new ContentCachingRequestWrapper for the given servlet request.
     *
     * @param request the original servlet request
     */
    public HttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        int contentLength = request.getContentLength();
        this.cachedContent = new ByteArrayOutputStream(contentLength >= 0 ? contentLength : 1024);
        this.contentCacheLimit = null;
    }

    /**
     * Create a new ContentCachingRequestWrapper for the given servlet request.
     *
     * @param request           the original servlet request
     * @param contentCacheLimit the maximum number of bytes to cache per request
     * @see #handleContentOverflow(int)
     * @since 4.3.6
     */
    public HttpServletRequestWrapper(HttpServletRequest request, int contentCacheLimit) {
        super(request);
        this.cachedContent = new ByteArrayOutputStream(contentCacheLimit);
        this.contentCacheLimit = contentCacheLimit;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = new ContentCachingInputStream(getRequest().getInputStream());
        }
        return this.inputStream;
    }

    @Override
    public String getCharacterEncoding() {
        String enc = super.getCharacterEncoding();
        return (enc != null ? enc : WebUtils.DEFAULT_CHARACTER_ENCODING);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (this.reader == null) {
            this.reader = new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
        }
        return this.reader;
    }

    @Override
    public String getParameter(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterMap();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterValues(name);
    }

    private boolean isFormPost() {
        String contentType = getContentType();
        return (contentType != null && contentType.contains(FORM_CONTENT_TYPE) && HttpMethod.POST.matches(getMethod()));
    }

    private void writeRequestParametersToCachedContent() {
        try {
            if (this.cachedContent.size() == 0) {
                String requestEncoding = getCharacterEncoding();
                Map<String, String[]> form = super.getParameterMap();
                for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
                    String name = nameIterator.next();
                    List<String> values = Arrays.asList(form.get(name));
                    for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
                        String value = valueIterator.next();
                        this.cachedContent.write(URLEncoder.encode(name, requestEncoding).getBytes());
                        if (value != null) {
                            this.cachedContent.write('=');
                            this.cachedContent.write(URLEncoder.encode(value, requestEncoding).getBytes());
                            if (valueIterator.hasNext()) {
                                this.cachedContent.write('&');
                            }
                        }
                    }
                    if (nameIterator.hasNext()) {
                        this.cachedContent.write('&');
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write request parameters to cached content", ex);
        }
    }

    /**
     * Return the cached request content as a byte array.
     * <p>
     * The returned array will never be larger than the content cache limit.
     */
    public byte[] getContentAsByteArray() {
        return this.cachedContent.toByteArray();
    }

    /**
     * Template method for handling a content overflow: specifically, a request
     * body being read that exceeds the specified content cache limit.
     * <p>
     * The default implementation is empty. Subclasses may override this to
     * throw a payload-too-large exception or the like.
     *
     * @param contentCacheLimit the maximum number of bytes to cache per request which has
     *                          just been exceeded
     * @since 4.3.6
     */
    protected void handleContentOverflow(int contentCacheLimit) {
    }

    private class ContentCachingInputStream extends ServletInputStream {

        private final ServletInputStream is;

        private boolean overflow = false;

        public ContentCachingInputStream(ServletInputStream is) {
            this.is = is;
        }

        @Override
        public int read() throws IOException {
            int ch = this.is.read();
            if (ch != -1 && !this.overflow) {
                if (contentCacheLimit != null && cachedContent.size() == contentCacheLimit) {
                    this.overflow = true;
                    handleContentOverflow(contentCacheLimit);
                } else {
                    cachedContent.write(ch);
                }
            }
            return ch;
        }

        @Override
        public boolean isFinished() {
            return this.is.isFinished();
        }

        @Override
        public boolean isReady() {
            return this.is.isReady();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            this.is.setReadListener(readListener);
        }
    }

}
