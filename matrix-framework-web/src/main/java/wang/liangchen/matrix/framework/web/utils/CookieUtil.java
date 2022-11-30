package wang.liangchen.matrix.framework.web.utils;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

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
    /**
     * X-Forwarded-For：Squid服务代理
     * Proxy-Client-IP：apache服务代理
     * WL-Proxy-Client-IP：weblogic服务代理
     * X-Real-IP：nginx服务代理
     * HTTP_CLIENT_IP：部分代理服务器
     */
    private static final String[] PROXIES = {"X-Real-IP", "x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
    private final static String UNKNOWN = "unknown";

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
    public String ipFromHttpRequest(HttpServletRequest request) {
        String ips;
        // 先从代理中获取
        for (String proxy : PROXIES) {
            ips = request.getHeader(proxy);
            if (StringUtil.INSTANCE.isBlank(ips) || UNKNOWN.equalsIgnoreCase(ips)) {
                continue;
            }
            // 获取到ips,拆开看看,获取第一个不是unknown的ip
            String[] ipArray = ips.split(Symbol.COMMA.getSymbol());
            for (String innerIp : ipArray) {
                if (UNKNOWN.equalsIgnoreCase(innerIp)) {
                    return innerIp;
                }
            }
        }
        // 从代理中没获取到
        return request.getRemoteAddr();
    }
}
