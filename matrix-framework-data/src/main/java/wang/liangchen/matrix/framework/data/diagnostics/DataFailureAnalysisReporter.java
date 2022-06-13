package wang.liangchen.matrix.framework.data.diagnostics;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalysisReporter;
import wang.liangchen.matrix.framework.data.dao.AbstractDao;

import java.util.Locale;

/**
 * @author Liangchen.Wang 2022-06-10 11:40
 */
public class DataFailureAnalysisReporter implements FailureAnalysisReporter {
    @Override
    public void report(FailureAnalysis analysis) {
        Throwable cause = analysis.getCause();
        if (!(cause instanceof NoSuchBeanDefinitionException)) {
            return;
        }
        NoSuchBeanDefinitionException noSuchBeanDefinitionException = (NoSuchBeanDefinitionException) cause;
        Class<?> beanType = noSuchBeanDefinitionException.getBeanType();
        if (AbstractDao.class.isAssignableFrom(beanType)) {
            System.out.println("Consider using '@EnableJdbc' in your configuration.");
        }
        if (Locale.CHINA == Locale.getDefault()) {
            System.out.println("建议添加注解'@EnableJdbc'");
        }
    }
}
