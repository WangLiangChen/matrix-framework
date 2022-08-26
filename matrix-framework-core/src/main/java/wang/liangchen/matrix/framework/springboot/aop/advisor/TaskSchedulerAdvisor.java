package wang.liangchen.matrix.framework.springboot.aop.advisor;

import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.aop.support.RootClassFilter;
import org.springframework.scheduling.TaskScheduler;

/**
 * @author Liangchen.Wang 2022-08-26 14:08
 */
public class TaskSchedulerAdvisor extends AbstractPointcutAdvisor {
    private final static Logger logger = LoggerFactory.getLogger(TaskSchedulerAdvisor.class);
    private final TaskSchedulerInterceptor taskSchedulerInterceptor;

    public TaskSchedulerAdvisor(TaskSchedulerInterceptor taskSchedulerInterceptor) {
        this.taskSchedulerInterceptor = taskSchedulerInterceptor;
    }

    @Override
    public Pointcut getPointcut() {
        return new TaskSchedulerPointcut();
    }

    @Override
    public Advice getAdvice() {
        return this.taskSchedulerInterceptor;
    }

    private static class TaskSchedulerPointcut implements Pointcut {
        @Override
        public ClassFilter getClassFilter() {
            return new RootClassFilter(TaskScheduler.class);
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
            nameMatchMethodPointcut.setMappedNames("schedule", "scheduleAtFixedRate", "scheduleWithFixedDelay");
            return nameMatchMethodPointcut;
        }
    }
}
