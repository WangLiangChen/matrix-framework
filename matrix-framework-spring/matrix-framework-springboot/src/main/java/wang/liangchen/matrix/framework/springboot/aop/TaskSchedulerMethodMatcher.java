package wang.liangchen.matrix.framework.springboot.aop;

import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2023-06-17 15:21
 */
public class TaskSchedulerMethodMatcher extends StaticMethodMatcher {
    private final static List<String> mappedNames = new ArrayList<>() {{
        add("execute");
        add("schedule");
        add("scheduleAtFixedRate");
        add("scheduleWithFixedDelay");
    }};

    @Override
    public boolean matches(Method method, @NonNull Class<?> targetClass) {
        return mappedNames.contains(method.getName());
    }
}
