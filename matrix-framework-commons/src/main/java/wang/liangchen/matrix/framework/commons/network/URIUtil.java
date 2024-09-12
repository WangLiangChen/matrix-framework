package wang.liangchen.matrix.framework.commons.network;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2021-08-19 19:46
 */
public enum URIUtil {
    /**
     * instance
     */
    INSTANCE;

    public URI toURI(String uriString, String... more) {
        ValidationUtil.INSTANCE.notBlank(MatrixExceptionLevel.WARN, uriString, "uriString must not be blank");
        try {
            return new URL(resolveURIString(uriString, more)).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return Paths.get(resolveURIString(uriString, more)).toUri();
        }
    }

    public URL toURL(String urlString, String... more) {
        ValidationUtil.INSTANCE.notBlank(MatrixExceptionLevel.WARN, urlString, "urlString must not be blank");
        try {
            return new URL(resolveURIString(urlString, more));
        } catch (MalformedURLException e) {
            try {
                return Paths.get(resolveURIString(urlString, more)).toUri().toURL();
            } catch (MalformedURLException ex) {
                throw new MatrixErrorException(ex);
            }
        }
    }

    public URI expandURI(URI uri, String... more) {
        ValidationUtil.INSTANCE.notNull(MatrixExceptionLevel.WARN, uri, "uri must not be null");
        return URI.create(resolveURIString(uri.toString(), more));
    }

    public URL expendURL(URL url, String... more) {
        ValidationUtil.INSTANCE.notNull(MatrixExceptionLevel.WARN, url, "url must not be null");
        try {
            return new URL(resolveURIString(url.toString(), more));
        } catch (MalformedURLException e) {
            throw new MatrixErrorException(e);
        }
    }


    public boolean isAvailableURL(URL url) {
        try {
            url.openStream();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String resolveURIString(String uriString, String... more) {
        // 把 windows路径中的 \\ 替换为 /
        if (uriString.contains(Symbol.DOUBLE_BACKSLASH.getSymbol())) {
            uriString = uriString.replaceAll(Symbol.DOUBLE_BACKSLASH.getSymbol(), Symbol.URI_SEPARATOR.getSymbol());
        }
        // 末尾加上 /
        uriString = uriString.endsWith(Symbol.URI_SEPARATOR.getSymbol()) ? uriString : uriString.concat(Symbol.URI_SEPARATOR.getSymbol());
        if (null == more || more.length == 0) {
            return uriString;
        }
        // 拼接 more
        return uriString.concat(resolveMore(more));
    }

    private String resolveMore(String... more) {
        if (null == more || more.length == 0) {
            return Symbol.BLANK.getSymbol();
        }
        for (int i = 0; i < more.length; i++) {
            // 把 \\ 替换为 /
            if (more[i].contains(Symbol.DOUBLE_BACKSLASH.getSymbol())) {
                more[i] = more[i].replaceAll(Symbol.DOUBLE_BACKSLASH.getSymbol(), Symbol.URI_SEPARATOR.getSymbol());
            }
            // 去除开头和结尾 /
            more[i] = more[i].replaceAll("^/*|/*$", Symbol.BLANK.getSymbol());
        }
        return Arrays.stream(more).collect(Collectors.joining(Symbol.URI_SEPARATOR.getSymbol()));
    }

    public String urlEncode(String url, Charset charset) {
        try {
            return URLEncoder.encode(url, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new MatrixErrorException(e);
        }
    }

    public String urlDecode(String url, Charset charset) {
        try {
            return URLDecoder.decode(url, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new MatrixErrorException(e);
        }
    }

}
