package wang.liangchen.matrix.framework.commons.network;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Liangchen.Wang
 */
public enum NetUtil {
    // instance;
    INSTANCE;
    private final InetAddress inetAddress;
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$");
    /**
     * X-Forwarded-For：Squid服务代理
     * Proxy-Client-IP：apache服务代理
     * WL-Proxy-Client-IP：weblogic服务代理
     * X-Real-IP：nginx服务代理
     * HTTP_CLIENT_IP：部分代理服务器
     */
    private static final String[] PROXIES = {"X-Real-IP", "x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
    private final static String UNKNOWN = "unknown";


    NetUtil() {
        try {
            inetAddress = getLocalInetAddress();
        } catch (SocketException e) {
            throw new MatrixErrorException(e);
        }
    }

    public String getLocalHostAddress() {
        return inetAddress.getHostAddress();
    }

    public String getLocalHostName() {
        return inetAddress.getHostName();
    }

    public boolean isConnectable(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String longToIpV4(long longIp) {
        // 4294967295=255.255.255.255
        if (longIp > 4294967295L) {
            return String.format("%d.%d.%d.%d/%d", (longIp >> 32) & 0xFF, (longIp >> 24) & 0xFF, (longIp >> 16) & 0xFF, (longIp >> 8) & 0xFF, longIp & 0xFF);
        }
        return String.format("%d.%d.%d.%d", (longIp >> 24) & 0xFF, (longIp >> 16) & 0xFF, (longIp >> 8) & 0xFF, longIp & 0xFF);
    }

    public long ipV4Lower16BitsToLong(String ip) {
        String[] ips = ip.split(Symbol.DOT_REGEX.getSymbol());
        int intIp = 0;
        // 第三组地址左移8位变成16位
        intIp |= Long.parseLong(ips[2]) << 8;
        intIp |= Long.parseLong(ips[3]);
        return intIp;
    }

    public long ipV4ToLong(String ip) {
        int maskIndex = ip.lastIndexOf('/');
        String prefix, mask = null;
        int index;
        // 没有掩码
        if (maskIndex == -1) {
            index = 3;
            prefix = ip;
        } else {
            index = 4;
            prefix = ip.substring(0, maskIndex);
            mask = ip.substring(maskIndex + 1);
        }
        // 拆分split
        String[] ipArray = prefix.split(Symbol.DOT_REGEX.getSymbol());
        long longIp = 0;
        for (String innerIp : ipArray) {
            longIp |= Long.parseLong(innerIp) << (8 * index--);
        }
        if (null == mask) {
            return longIp;
        }
        longIp |= Long.parseLong(mask);
        return longIp;
    }

    public Long netAddress(String ip, Byte mask) {
        Long ipLong = NetUtil.INSTANCE.ipV4ToLong(ip);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (i < mask) {
                builder.append("1");
            } else {
                builder.append("0");
            }
        }
        Long maskLong = Long.parseLong(builder.toString(), 2);
        return ipLong & maskLong;
    }


    public boolean isIPv4Private(String ip) {
        long longIp = ipV4ToLong(ip);
        return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255"))
                || (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255"))
                || longIp >= ipV4ToLong("192.168.0.0") && longIp <= ipV4ToLong("192.168.255.255");
    }

    public boolean isIPv4(String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
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

    public Map<String, String> queryString2Map(String queryString) {
        if (StringUtil.INSTANCE.isBlank(queryString)) {
            return Collections.emptyMap();
        }
        String[] kvs = queryString.split(Symbol.AND.getSymbol());
        String[] kv;
        Map<String, String> result = new HashMap<>(kvs.length);
        for (int i = 0; i < kvs.length; i++) {
            kv = kvs[i].split(Symbol.EQUAL.getSymbol());
            if (kv.length == 1) {
                result.put(kv[0], "");
            } else {
                result.put(kv[0], kv[1]);
            }
        }
        return result;
    }

    private InetAddress getLocalInetAddress() throws SocketException {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress = null;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                // 排除loopback
                if (networkInterface.isLoopback()) {
                    continue;
                }
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLinkLocalAddress() && !inetAddress.isLoopbackAddress() && !inetAddress.isAnyLocalAddress()) {
                        break;
                    }
                }
            }
            return inetAddress;
        }
    }
}
