package wang.liangchen.matrix.framework.springboot.startup;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.ConfigurableEnvironment;
import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.commons.utils.StopWatch;
import wang.liangchen.matrix.framework.springboot.context.BeanLoader;
import wang.liangchen.matrix.framework.springboot.env.EnvironmentContext;

import static wang.liangchen.matrix.framework.springboot.startup.StartupStatic.DEFAULT_SCAN_PACKAGES;
import static wang.liangchen.matrix.framework.springboot.startup.StartupStatic.excludeScanPackages;


public class StartupProcessor implements
        ApplicationContextInitializer<ConfigurableApplicationContext>,
        ApplicationListener<ApplicationEvent>,
        EnvironmentPostProcessor,
        FailureAnalyzer {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        StopWatch.WatchTask startupTask = StartupStatic.stopWatch.startTask("Startup");
        // 可以使用下面的语句判断是否springcloud
        // boolean containsBootstrap = applicationContext.getEnvironment().getPropertySources().contains("bootstrap");
        // 初始化BeanLoader
        BeanLoader.INSTANCE.resetApplicationContext(applicationContext);
        startupTask.addMessage("reset ApplicationContext to BeanLoader");
        // sacan package
        hanldeScanPackages(applicationContext);
        startupTask.addMessage("Add scan package: " + DEFAULT_SCAN_PACKAGES + ", and exclude packages: " + excludeScanPackages);
        startupTask.prettyPrint(true);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("--- StartupProcessor: onApplicationEvent(" + event.getClass().getSimpleName() + ")");
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 这里可以最早的获取到初始完成的environment,包括Profiles,设置到全局上下文
        EnvironmentContext.INSTANCE.setEnvironment(environment);
    }

    @Override
    public FailureAnalysis analyze(Throwable failure) {
        return null;
    }

    private void hanldeScanPackages(ConfigurableApplicationContext applicationContext) {
        // 扫描框架包和排除框架子包(如matrix-cache)
        BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanRegistry);
        scanner.setResourceLoader(applicationContext);
        // 获取StartProcessRunListener中设置的要排除扫描的包
        if (CollectionUtil.INSTANCE.isNotEmpty(excludeScanPackages)) {
            scanner.addExcludeFilter((metadataReader, metadataReaderFactory) -> {
                String className = metadataReader.getClassMetadata().getClassName();
                for (String excludeScanPackage : excludeScanPackages) {
                    return className.startsWith(excludeScanPackage);
                }
                return false;
            });
        }
        scanner.scan(DEFAULT_SCAN_PACKAGES);
    }

}
