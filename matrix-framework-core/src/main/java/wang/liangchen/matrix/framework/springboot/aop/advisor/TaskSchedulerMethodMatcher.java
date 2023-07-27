package wang.liangchen.matrix.framework.springboot.aop.advisor;

import org.springframework.aop.support.StaticMethodMatcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2023-06-17 15:21
 */
public class TaskSchedulerMethodMatcher extends StaticMethodMatcher {
    private final static List<String> mappedNames = new ArrayList<String>() {{
        add("execute");
        add("schedule");
        add("scheduleAtFixedRate");
        add("scheduleWithFixedDelay");
    }};

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return mappedNames.contains(method.getName());
    }
}
