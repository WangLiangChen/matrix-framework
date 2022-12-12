package wang.liangchen.matrix.framework.springboot.env;

import org.springframework.boot.Banner;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePatternResolver;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LiangChen.Wang 2021/7/2
 */
@SuppressWarnings("NullableProblems")
public class StartProcessRunListener implements SpringApplicationRunListener, Ordered {
    private final static String DEFAULT_SCAN_PACKAGES = "wang.liangchen.matrix";
    private final static String STARTING = Symbol.LINE_SEPARATOR.getSymbol() + "------------------------------------> Matrix Framework is Starting <------------------------------------" + Symbol.LINE_SEPARATOR.getSymbol();
    private final static String CLOSED = Symbol.LINE_SEPARATOR.getSymbol() + "------------------------------------> Matrix Framework is Closed <------------------------------------" + Symbol.LINE_SEPARATOR.getSymbol();
    private final static String SPRING_CONFIG_IMPORT = "spring.config.import";
    static Set<String> excludeScanPackages;

    public StartProcessRunListener(SpringApplication springApplication, String[] args) {
        if (null == springApplication) {
            return;
        }
        // 注册一个关闭钩子，打印系统关闭信息
        SpringApplication.getShutdownHandlers().add(() -> System.out.println(CLOSED));
        // 打印系统开始启动信息
        System.out.println(STARTING);

        PrettyPrinter.INSTANCE.buffer("args are:{}", Arrays.asList(args).toString());
        springApplication.setBannerMode(Banner.Mode.OFF);
        PrettyPrinter.INSTANCE.buffer("set BannerMode=OFF");

        // 需要排除主动扫描的包,防止wang.liangchen.matrix包被重复扫描
        // 因为在使用到这个值的地方，跟当前对象不是一个对象，所以用类变量传递
        Set<Object> allSources = springApplication.getAllSources();
        excludeScanPackages = allSources.stream().map(e -> ((Class<?>) e).getPackage().getName())
                .filter(e -> e.startsWith(DEFAULT_SCAN_PACKAGES)).collect(Collectors.toSet());
        PrettyPrinter.INSTANCE.buffer("set excludeScanPackages={}", excludeScanPackages);
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener").flush();
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        // 设置默认的配置根路径 classpath
        environment.getSystemProperties().put(SPRING_CONFIG_IMPORT, "matrix://".concat(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX));
        PrettyPrinter.INSTANCE.buffer("set spring.config.import={}", environment.getProperty(SPRING_CONFIG_IMPORT));
        PrettyPrinter.INSTANCE.flush();
    }


    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener").flush();
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener").flush();
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.buffer("duration:" + timeTaken.getSeconds());
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.buffer("duration:" + timeTaken.getSeconds());
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener").flush();
        exception.printStackTrace();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
