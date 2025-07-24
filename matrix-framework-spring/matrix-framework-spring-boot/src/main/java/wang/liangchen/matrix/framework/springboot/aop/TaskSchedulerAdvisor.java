package wang.liangchen.matrix.framework.springboot.aop;

import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.lang.NonNull;

/**
 * @author Liangchen.Wang 2022-08-26 14:08
 */
public class TaskSchedulerAdvisor extends AbstractPointcutAdvisor {
    private final static Logger logger = LoggerFactory.getLogger(TaskSchedulerAdvisor.class);
    private final static Pointcut taskSchedulerPointcut = new TaskSchedulerPointcut();
    private final Advice taskSchedulerInterceptor;

    public TaskSchedulerAdvisor(TaskSchedulerMethodAdvice taskSchedulerInterceptor) {
        this.taskSchedulerInterceptor = taskSchedulerInterceptor;
    }

    @NonNull
    @Override
    public Pointcut getPointcut() {
        return taskSchedulerPointcut;
    }

    @NonNull
    @Override
    public Advice getAdvice() {
        return this.taskSchedulerInterceptor;
    }

}
