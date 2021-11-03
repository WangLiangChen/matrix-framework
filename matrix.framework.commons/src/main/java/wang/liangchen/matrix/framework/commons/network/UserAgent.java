package wang.liangchen.matrix.framework.commons.network;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LiangChen.Wang 2020/11/18
 */
public class UserAgent {
    private Os os;
    private Browser browser;
    private String userAgent;
    private static Set<UserAgent> set = new HashSet<>();

    private UserAgent(Os os, Browser browser, String userAgent) {
        this.os = os;
        this.browser = browser;
        this.userAgent = userAgent;
    }

    static {
        set.add(new UserAgent(Os.Windows, Browser.Chrome, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36"));
        set.add(new UserAgent(Os.Windows, Browser.Edge, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/18.17763"));
        set.add(new UserAgent(Os.Windows, Browser.IE, "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko"));
        set.add(new UserAgent(Os.Mac, Browser.Chrome, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36"));
        set.add(new UserAgent(Os.Mac, Browser.Firefox, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:65.0) Gecko/20100101 Firefox/65.0"));
        set.add(new UserAgent(Os.Mac, Browser.Safari, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0.3 Safari/605.1.15"));
        set.add(new UserAgent(Os.iOS, Browser.Chrome, "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_4 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) CriOS/31.0.1650.18 Mobile/11B554a Safari/8536.25"));
        set.add(new UserAgent(Os.iOS, Browser.Safari, "Mozilla/5.0 (iPhone; CPU iPhone OS 8_3 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Version/8.0 Mobile/12F70 Safari/600.1.4"));
        set.add(new UserAgent(Os.Android, Browser.Chrome, "Mozilla/5.0 (Linux; Android 4.2.1; M040 Build/JOP40D) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.59 Mobile Safari/537.36"));
        set.add(new UserAgent(Os.Android, Browser.Webkit, "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-cn; M351 Build/KTU84P) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"));
        set.add(new UserAgent(Os.Linux, Browser.Chrome, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36"));
        set.add(new UserAgent(Os.Linux, Browser.Firefox, "Mozilla/5.0 (X11; Linux x86_64; rv:68.0) Gecko/20100101 Firefox/68.0"));
    }

    public static Set<String> byOs(Os... os) {
        return set.stream().filter(e -> {
            for (Os o : os) {
                if (e.getOs() == o) {
                    return true;
                }
            }
            return false;
        }).map(UserAgent::getUserAgent).collect(Collectors.toSet());
    }

    public static String byOsRandom(Os... os) {
        Set<String> set = byOs(os);
        return set.stream().findAny().get();
    }

    public Os getOs() {
        return os;
    }

    public Browser getBrowser() {
        return browser;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public enum Os {
        Mac, Linux, Windows, iOS, Android;
    }

    public enum Browser {
        Chrome, Firefox, Safari, Edge, IE, Webkit;
    }

    public static void main(String[] args) {
        Set<String> userAgents = UserAgent.byOs(Os.Windows, Os.Mac);
        System.out.println(userAgents);
        String userAgent = UserAgent.byOsRandom(Os.Windows, Os.Mac);
        System.out.println(userAgent);
    }
}
