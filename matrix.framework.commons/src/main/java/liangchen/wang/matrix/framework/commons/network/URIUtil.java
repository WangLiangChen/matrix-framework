package liangchen.wang.matrix.framework.commons.network;

import liangchen.wang.matrix.framework.commons.enumeration.Symbol;
import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
        try {
            if (null == more || more.length == 0) {
                return Paths.get(uriString).toUri();
            }
            return Paths.get(resolveFirst(uriString), resolveMore(resolveMore(more))).toUri();
        } catch (InvalidPathException ex) {
            if (null == more || more.length == 0) {
                return URI.create(resolveFirst(uriString));
            }
            return URI.create(resolveFirst(uriString)).resolve(resolveMore(more));
        }
    }

    public URI expandURI(URI uri, String... more) {
        if (null == more || more.length == 0) {
            return uri;
        }
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
        if (null == more || more.length == 0) {
            return url;
        }
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

    private String resolveFirst(String uriString) {
        // 把 \\ 替换为 /
        uriString = uriString.replaceAll("\\\\", Symbol.URI_SEPARATOR.getSymbol());
        // 末尾加上 /
        return uriString.endsWith(Symbol.URI_SEPARATOR.getSymbol()) ? uriString : uriString.concat(Symbol.URI_SEPARATOR.getSymbol());
    }

    private String resolveMore(String... more) {
        for (int i = 0; i < more.length; i++) {
            // 把 \\ 替换为 / 去除开头和结尾 /
            more[i] = more[i].replaceAll("\\\\", Symbol.URI_SEPARATOR.getSymbol()).replaceAll("^/*|/*$", Symbol.BLANK.getSymbol());
        }
        return Arrays.stream(more).collect(Collectors.joining(Symbol.URI_SEPARATOR.getSymbol()));
    }

}
