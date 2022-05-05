package wang.liangchen.matrix.framework.commons.network;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.InvalidPathException;
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
        Assert.INSTANCE.notBlank(uriString, "uriString can't be blank");
        uriString = resolveURIString(uriString);
        String moreString = resolveMore(more);
        try {
            return Paths.get(uriString, moreString).toUri();
        } catch (InvalidPathException ex) {
            return URI.create(uriString).resolve(moreString);
        }
    }

    public URI expandURI(URI uri, String... more) {
        return uri.resolve(resolveMore(more));
    }

    public URL toURL(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new MatrixErrorException(e);
        }
    }

    public URL toURL(String urlString, String... more) {
        URI uri = toURI(urlString, more);
        return toURL(uri);
    }


    public URL expendURL(URL url, String... more) {
        try {
            return new URL(url, resolveMore(more));
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

    private String resolveURIString(String uriString) {
        // 把 \\ 替换为 /
        uriString = uriString.replaceAll(Symbol.DOUBLE_BACKSLASH.getSymbol(), Symbol.URI_SEPARATOR.getSymbol());
        // 末尾加上 /
        return uriString.endsWith(Symbol.URI_SEPARATOR.getSymbol()) ? uriString : uriString.concat(Symbol.URI_SEPARATOR.getSymbol());
    }

    private String resolveMore(String... more) {
        if (null == more || more.length == 0) {
            return Symbol.BLANK.getSymbol();
        }
        for (int i = 0; i < more.length; i++) {
            // 把 \\ 替换为 / 去除开头和结尾 /
            more[i] = more[i].replaceAll(Symbol.DOUBLE_BACKSLASH.getSymbol(), Symbol.URI_SEPARATOR.getSymbol()).replaceAll("^/*|/*$", Symbol.BLANK.getSymbol());
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
