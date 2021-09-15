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
                return URI.create(uriString);
            }
            return URI.create(resolveFirst(uriString)).resolve(resolveMore(more));
        }
    }

    public URI expandURI(URI uri, String... more) {
        if (null == more || more.length == 0) {
            return uri;
        }
        return toURI(uri.toString(), more);
    }

    public URL toURL(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new MatrixErrorException(e);
        }
    }

    public URL toURL(String string, String... more) {
        return toURL(toURI(string, more));
    }


    public URL expendURL(URL url, String... more) {
        if (null == more || more.length == 0) {
            return url;
        }
        return toURL(url.toString(), more);
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
        uriString = uriString.replaceAll("\\\\", "/");
        // 末尾加上 /
        return uriString.endsWith(Symbol.URI_SEPARATOR.getSymbol()) ? uriString : uriString.concat(Symbol.URI_SEPARATOR.getSymbol());
    }

    private String resolveMore(String... more) {
        for (int i = 0; i < more.length; i++) {
            // 把 \\ 替换为 / 去除开头和结尾 /
            more[i] = more[i].replaceAll("\\\\", "/").replaceAll("^/*|/*$", "");
        }
        return Arrays.stream(more).collect(Collectors.joining(Symbol.URI_SEPARATOR.getSymbol()));
    }

}
