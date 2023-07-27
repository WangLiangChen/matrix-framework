package wang.liangchen.matrix.framework.springboot.aop.advisor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.RootClassFilter;
import org.springframework.scheduling.TaskScheduler;

/**
 * @author Liangchen.Wang 2023-06-17 15:02
 */
public class TaskSchedulerPointcut implements Pointcut {
    private final static ClassFilter classFilter = new RootClassFilter(TaskScheduler.class);
    private final static TaskSchedulerMethodMatcher methodMatcher = new TaskSchedulerMethodMatcher();


    @Override
    public ClassFilter getClassFilter() {
        return classFilter;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }
}