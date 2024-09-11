package wang.liangchen.matrix.framework.springboot.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class StartupProcessor implements
        ApplicationContextInitializer<ConfigurableApplicationContext>,
        ApplicationListener<ApplicationEvent>,
        EnvironmentPostProcessor,
        FailureAnalyzer {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

    }

    @Override
    public FailureAnalysis analyze(Throwable failure) {
        return null;
    }

}
