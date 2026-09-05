package wang.liangchen.matrix.framework.ddd.support.snowflake;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 基于本机IPv4低16位计算workerId的策略。
 * 将IP的第3、4段组合为16位整数，再按位与10位掩码（0x3FF）映射到workerId空间[0, 1023]。
 * <p>
 * <b>局限性（使用前必读）</b>：本策略仅适合小规模、IP段受控的部署，
 * 无法保证workerId全局唯一：
 * <ul>
 *   <li>低16位相同的两台机器（如192.168.1.5与10.0.1.5）必然得到相同workerId；</li>
 *   <li>多网卡环境（Docker/K8s宿主机常见eth0/docker0/cni0并存）下网卡枚举顺序不保证，
 *       重启后可能选中不同网卡导致workerId漂移；</li>
 *   <li>无site-local IPv4时回退到{@code InetAddress.getLocalHost()}，
 *       可能返回127.0.0.1（多机同workerId）或IPv6地址（抛出异常）；</li>
 *   <li>无任何冲突检测与协调机制。</li>
 * </ul>
 * 多实例部署建议使用{@link FixedWorkerIdStrategy}（K8s StatefulSet序号等外部协调），
 * 或自行实现带协调机制的{@link WorkerIdStrategy}。
 *
 * @author Liangchen.Wang
 */
public class IpWorkerIdStrategy implements WorkerIdStrategy {

    private static final System.Logger logger = System.getLogger(IpWorkerIdStrategy.class.getName());

    private static final long WORKER_ID_MASK = Snowflake.MAX_WORKER_ID;

    @Override
    public long resolveWorkerId() {
        String localIp = getLocalHostAddress();
        long lower16 = ipV4Lower16BitsToLong(localIp);
        return lower16 & WORKER_ID_MASK;
    }

    private String getLocalHostAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isSiteLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
            String fallback = InetAddress.getLocalHost().getHostAddress();
            if (fallback.startsWith("127.") || fallback.equals("::1") || fallback.equals("0:0:0:0:0:0:0:1")) {
                logger.log(System.Logger.Level.WARNING,
                        "No site-local IPv4 found, fallback to loopback {0}. "
                                + "All instances will get the same workerId. "
                                + "Consider using FixedWorkerIdStrategy instead.",
                        fallback);
            }
            return fallback;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to resolve local host address", e);
        }
    }

    static long ipV4Lower16BitsToLong(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ip);
        }
        long result = 0;
        result |= Long.parseLong(parts[2]) << 8;
        result |= Long.parseLong(parts[3]);
        return result;
    }
}