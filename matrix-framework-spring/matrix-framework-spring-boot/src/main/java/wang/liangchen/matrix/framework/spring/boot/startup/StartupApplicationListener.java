package wang.liangchen.matrix.framework.spring.boot.startup;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.utils.StopWatch;
import wang.liangchen.matrix.framework.spring.boot.context.BeanContext;
import wang.liangchen.matrix.framework.spring.boot.context.EnvironmentContext;

import java.util.Set;
import java.util.stream.Collectors;

import static wang.liangchen.matrix.framework.spring.boot.startup.StartupStopWatch.stopWatch;
import static wang.liangchen.matrix.framework.spring.boot.startup.StartupStopWatch.watchTask;


public final class StartupApplicationListener implements ApplicationListener<ApplicationEvent> {
    private static boolean runned = false;

    private final static String DEFAULT_SCAN_PACKAGES = "wang.liangchen.matrix.framework";

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
        if (event instanceof ApplicationFailedEvent) {
            onApplicationFailedEvent((ApplicationFailedEvent) event);
        }
    }

    private void onApplicationStartingEvent(ApplicationStartingEvent event) {
        if (runned) {
            return;
        }
        runned = true;
        watchTask.addMessage("Matrix Framework is staring...");
        registerShutdownHook();
        event.getSpringApplication().setBannerMode(Banner.Mode.OFF);
    }

    private void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        boolean isSpringCloud = environment.getPropertySources().contains("bootstrap");
        if (isSpringCloud) {
            return;
        }
        EnvironmentContext.INSTANCE.resetEnvironmentContext(environment);
        watchTask.addMessage("Environment is prepared, Set environment to EnvironmentContext");

    }

    private void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        boolean isSpringCloud = applicationContext.getEnvironment().getPropertySources().contains("bootstrap");
        if (isSpringCloud) {
            return;
        }
        SpringApplication springApplication = event.getSpringApplication();
        // scanMatrixPackages(springApplication, applicationContext);
        BeanContext.INSTANCE.resetApplicationContext(applicationContext);
        watchTask.addMessage("ApplicationContext is initialized, Set applicationContext to BeanContext");
        watchTask.prettyPrint();
    }

    private void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
    }

    private void onContextRefreshedEvent(ContextRefreshedEvent event) {
        watchTask.prettyPrint();
    }

    private void onApplicationStartedEvent(ApplicationStartedEvent event) {
    }

    private void onApplicationReadyEvent(ApplicationReadyEvent event) {
        watchTask.addMessage("Matrix Framework has been started!");
        watchTask.stop();
        watchTask.prettyPrint();
    }

    private void onContextClosedEvent(ContextClosedEvent event) {
    }

    private void onApplicationFailedEvent(ApplicationFailedEvent event) {
    }

    private void registerShutdownHook() {
        // 注册一个虚拟机关闭钩子,监听虚拟机关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            StopWatch.WatchTask closeTask = stopWatch.startTask("Close");
            closeTask.addMessage("JVM is closing...");
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
        Set<String> excludedPackages = springApplication.getAllSources().stream()
                .map(e -> ((Class<?>) e).getPackage().getName())
                .filter(e -> e.startsWith(DEFAULT_SCAN_PACKAGES)).collect(Collectors.toSet());
        BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        // register matrix
        AutoConfigurationPackages.register(beanRegistry, DEFAULT_SCAN_PACKAGES);
        // 扫描框架包和排除框架子包(如matrix-cache)
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanRegistry);
        scanner.setResourceLoader(applicationContext);
        if (CollectionUtil.INSTANCE.isNotEmpty(excludedPackages)) {
            scanner.addExcludeFilter((metadataReader, metadataReaderFactory) -> {
                String className = metadataReader.getClassMetadata().getClassName();
                for (String excludedPackage : excludedPackages) {
                    return className.startsWith(excludedPackage);
                }
                return false;
            });
        }
        scanner.scan(DEFAULT_SCAN_PACKAGES);
        watchTask.addMessage("Scan Matrix's packages: " + DEFAULT_SCAN_PACKAGES + ", but exclude packages: " + excludedPackages);
    }
}
