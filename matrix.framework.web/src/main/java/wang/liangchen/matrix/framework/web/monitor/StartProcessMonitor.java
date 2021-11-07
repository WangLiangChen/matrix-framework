package wang.liangchen.matrix.framework.web.monitor;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;
import wang.liangchen.matrix.framework.web.annotation.EnableWeb;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class StartProcessMonitor implements SpringApplicationRunListener {
    public StartProcessMonitor() {
    }

    public StartProcessMonitor(SpringApplication springApplication, String[] args) {
        handleEnableWeb(springApplication);
    }

    private void handleEnableWeb(SpringApplication springApplication) {
        PrettyPrinter.INSTANCE.buffer("Scan annotated @EnableWeb classes");
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(EnableWeb.class));
        Set<Object> allSources = springApplication.getAllSources();
        Set<BeanDefinition> candidates = allSources.stream()
                .map(e -> (Class<?>) e)
                .map(e -> e.getPackage().getName())
                .map(provider::findCandidateComponents)
                .flatMap(Collection::stream).collect(Collectors.toSet());
        Class<?> clazz = null;
        for (BeanDefinition beanDefinition : candidates) {
            String beanClassName = beanDefinition.getBeanClassName();
            try {
                clazz = ClassUtils.forName(beanClassName, this.getClass().getClassLoader());
                break;
            } catch (ClassNotFoundException e) {
                throw new MatrixErrorException(e);
            }
        }
        if (null == clazz) {
            PrettyPrinter.INSTANCE.buffer("No class has annotation @EnableWeb");
            PrettyPrinter.INSTANCE.flush();
            return;
        }
        EnableWeb enableWeb = AnnotatedElementUtils.getMergedAnnotation(clazz, EnableWeb.class);
        PrettyPrinter.INSTANCE.buffer("{} has annotation @EnableWeb:{}", clazz.getName(), enableWeb.webType());
        PrettyPrinter.INSTANCE.flush();
        if (EnableWeb.WebType.WEBFLUX == enableWeb.webType()) {
            springApplication.setWebApplicationType(WebApplicationType.REACTIVE);
        }
    }
}
