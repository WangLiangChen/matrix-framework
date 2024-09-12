package wang.liangchen.matrix.framework.springboot.startup;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePatternResolver;
import wang.liangchen.matrix.framework.commons.utils.StopWatch;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

public class StartupRunListener implements SpringApplicationRunListener, Ordered {
    private final static String SPRING_CONFIG_IMPORT = "spring.config.import";
    final static String DEFAULT_SCAN_PACKAGES = "wang.liangchen.matrix";
    static Set<String> excludeScanPackages;

    public StartupRunListener(SpringApplication springApplication, String[] args) {
        if (null == springApplication) {
            return;
        }
        StopWatch.WatchTask closeTask = StartupStatic.stopWatch.startTask("Close");
        // 注册一个虚拟机关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            closeTask.addMessage("Matrix Framework is closing...");
            closeTask.prettyPrint(true);
        }));
        // 注册一个Spring关闭钩子
        SpringApplication.getShutdownHandlers().add(() -> {
            closeTask.addMessage("Matrix Framework has been closed!");
            closeTask.stop();
            closeTask.prettyPrint(true);
        });
        // 开始启动
        StopWatch.WatchTask startupTask = StartupStatic.stopWatch.startTask("Startup");
        startupTask.addMessage("Matrix Framework is starting...");

        // wang.liangchen.matrix包在后面会被自动扫描
        // 如果使用matrix-framework的其它系统(如matrix-cache)的包也是wang.liangchen.matrix,则会被重复扫描
        // 所以这里要排除重复扫描
        // 因为在使用到excludeScanPackages的地方，跟当前对象不是一个对象，所以用类变量传递
        Set<Object> allSources = springApplication.getAllSources();
        excludeScanPackages = allSources.stream().map(e -> ((Class<?>) e).getPackage().getName())
                .filter(e -> e.startsWith(DEFAULT_SCAN_PACKAGES)).collect(Collectors.toSet());
        startupTask.addMessage("Exclude scanned packages: " + excludeScanPackages);
        startupTask.prettyPrint(true);
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {

    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        // 设置默认的配置根路径 classpath*, 为了必须至少调用一次ConfigDataLoader来初始化EnvironmentContext
        environment.getSystemProperties().put(SPRING_CONFIG_IMPORT, "matrix://".concat(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX));
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("--- StartupRunListener: contextPrepared");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("--- StartupRunListener: contextLoaded");
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        System.out.println("--- StartupRunListener: started");
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        System.out.println("--- StartupRunListener: ready");
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("--- StartupRunListener: failed");
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}
