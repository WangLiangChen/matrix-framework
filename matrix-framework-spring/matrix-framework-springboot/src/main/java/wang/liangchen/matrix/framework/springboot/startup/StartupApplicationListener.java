package wang.liangchen.matrix.framework.springboot.startup;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.commons.utils.StopWatch;
import wang.liangchen.matrix.framework.springboot.context.BeanContext;
import wang.liangchen.matrix.framework.springboot.context.EnvironmentContext;

import java.util.Set;
import java.util.stream.Collectors;


public final class StartupApplicationListener implements ApplicationListener<ApplicationEvent> {
    private static boolean runned = false;
    private final static StopWatch stopWatch = new StopWatch();
    public final static StopWatch.WatchTask startupTask = stopWatch.startTask("Startup");
    private final static String DEFAULT_SCAN_PACKAGES = "wang.liangchen.matrix";

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartingEvent) {
            onApplicationStartingEvent((ApplicationStartingEvent) event);
        }
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            onApplicationEnvironmentPreparedEvent((ApplicationEnvironmentPreparedEvent) event);
        }
        if (event instanceof ApplicationContextInitializedEvent) {
            onApplicationContextInitializedEvent((ApplicationContextInitializedEvent) event);
        }
        if (event instanceof ApplicationPreparedEvent) {
            onApplicationPreparedEvent((ApplicationPreparedEvent) event);
        }
        if (event instanceof ContextRefreshedEvent) {
            onContextRefreshedEvent((ContextRefreshedEvent) event);
        }
        if (event instanceof ApplicationStartedEvent) {
            onApplicationStartedEvent((ApplicationStartedEvent) event);
        }
        if (event instanceof ApplicationReadyEvent) {
            onApplicationReadyEvent((ApplicationReadyEvent) event);
        }
        if (event instanceof ContextClosedEvent) {
            onContextClosedEvent((ContextClosedEvent) event);
        }
    }

    private void onApplicationStartingEvent(ApplicationStartingEvent event) {
        if (runned) {
            return;
        }
        runned = true;
        startupTask.addMessage("Matrix Framework is staring...");
        registerShutdownHook();
        event.getSpringApplication().setBannerMode(Banner.Mode.OFF);
    }

    private void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        boolean isSpringCloud = environment.getPropertySources().contains("bootstrap");
        if (isSpringCloud) {
            return;
        }
        EnvironmentContext.INSTANCE.setEnvironment(environment);
        startupTask.addMessage("Set environment to EnvironmentContext");
        startupTask.prettyPrint();

    }

    private void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        boolean isSpringCloud = applicationContext.getEnvironment().getPropertySources().contains("bootstrap");
        if (isSpringCloud) {
            return;
        }
        SpringApplication springApplication = event.getSpringApplication();
        scanMatrixPackages(springApplication, applicationContext);
        BeanContext.INSTANCE.resetApplicationContext(applicationContext);
        startupTask.addMessage("Set applicationContext to BeanContext");
        startupTask.prettyPrint();
    }

    private void onApplicationPreparedEvent(ApplicationPreparedEvent event) {

    }

    private void onContextRefreshedEvent(ContextRefreshedEvent event) {

    }

    private void onApplicationStartedEvent(ApplicationStartedEvent event) {

    }

    private void onApplicationReadyEvent(ApplicationReadyEvent event) {
        startupTask.addMessage("Matrix Framework has been started");
        startupTask.stop();
        startupTask.prettyPrint();
    }

    private void onContextClosedEvent(ContextClosedEvent event) {

    }

    private void registerShutdownHook() {
        // 注册一个虚拟机关闭钩子,监听虚拟机关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            StopWatch.WatchTask closeTask = stopWatch.startTask("Close");
            closeTask.addMessage("Matrix Framework is closing...");
            closeTask.prettyPrint();
        }));
        // 注册一个Spring关闭钩子,监听Spring关闭
        SpringApplication.getShutdownHandlers().add(() -> {
            StopWatch.WatchTask closeTask = stopWatch.startTask("Close");
            closeTask.addMessage("Matrix Framework has been closed!");
            closeTask.stop();
            closeTask.prettyPrint();
        });
    }

    private void scanMatrixPackages(SpringApplication springApplication, ConfigurableApplicationContext applicationContext) {
        // 排除多余的matrix包，扫描默认的matrix包
        Set<String> excludePackages = springApplication.getAllSources().stream()
                .map(e -> ((Class<?>) e).getPackage().getName())
                .filter(e -> e.startsWith(DEFAULT_SCAN_PACKAGES)).collect(Collectors.toSet());
        // 扫描框架包和排除框架子包(如matrix-cache)
        BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanRegistry);
        scanner.setResourceLoader(applicationContext);
        if (CollectionUtil.INSTANCE.isNotEmpty(excludePackages)) {
            scanner.addExcludeFilter((metadataReader, metadataReaderFactory) -> {
                String className = metadataReader.getClassMetadata().getClassName();
                for (String excludeScanPackage : excludePackages) {
                    return className.startsWith(excludeScanPackage);
                }
                return false;
            });
        }
        scanner.scan(DEFAULT_SCAN_PACKAGES);
        startupTask.addMessage("Scan Matrix's packages: " + DEFAULT_SCAN_PACKAGES + ", and excluded packages: " + excludePackages);
    }
}
