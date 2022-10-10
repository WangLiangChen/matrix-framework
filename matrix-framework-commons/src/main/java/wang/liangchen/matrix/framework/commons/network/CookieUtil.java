package wang.liangchen.matrix.framework.commons.network;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * @author Liangchen.Wang
 */
public enum CookieUtil {
    // instance;
    INSTANCE;

    public String getCookieValue(HttpServletRequest request, String cookieName) {
        return getCookieValue(request, cookieName, null);
    }

    public String getCookieValue(HttpServletRequest request, String cookieName, Charset encodeCharset) {
        if (null == cookieName) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (null == cookies || cookies.length == 0) {
            return null;
        }
        String cookieValue = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                cookieValue = cookie.getValue();
            }
        }
        if (null == cookieValue) {
            return null;
        }
        try {
            cookieValue = null == encodeCharset ? cookieValue : URLDecoder.decode(cookieValue, encodeCharset.name());
        } catch (UnsupportedEncodingException e) {
            throw new MatrixErrorException(e);
        }
        return cookieValue;
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        setCookie(request, response, cookieName, null, 0, null);
    }

    public void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
        setCookie(request, response, cookieName, cookieValue, -1);
    }

    public void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxAge) {
        setCookie(request, response, cookieName, cookieValue, cookieMaxAge, null);
    }

    public void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxAge, Charset encodeCharset) {
        cookieValue = null == cookieValue ? "" : cookieValue;
        try {
            cookieValue = null == encodeCharset ? cookieValue : URLEncoder.encode(cookieValue, encodeCharset.name());
        } catch (UnsupportedEncodingException e) {
            throw new MatrixErrorException(e);
        }
        Cookie cookie = new Cookie(cookieName, cookieValue);
        // <0 关闭浏览器失效；=0 删除cookie
        cookie.setMaxAge(cookieMaxAge);
        cookie.setPath("/");
        if (null != request) {
            cookie.setDomain(getDomainName(request));
        }
        response.addCookie(cookie);
    }

    public String getDomainName(HttpServletRequest request) {
        return request.getServerName().concat(":").concat(String.valueOf(request.getServerPort()));
    }
}
