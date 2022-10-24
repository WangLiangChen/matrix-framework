package wang.liangchen.matrix.framework.springboot.aop.advisor;

import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.PatternMatchUtils;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-10-24 13:57
 */
public class AnnotationOrNameMatchMethodPointcut extends StaticMethodMatcher {
    private final Class<? extends Annotation> annotationType;
    private final boolean checkInherited;
    private final Set<MappedMethod> mappedMethods;

    private Set<Class<? extends Annotation>> excludedAnnotationTypes;

    public AnnotationOrNameMatchMethodPointcut(Class<? extends Annotation> annotationType, boolean checkInherited, Set<MappedMethod> mappedMethods) {
        this.checkInherited = checkInherited;
        this.annotationType = annotationType;
        this.mappedMethods = mappedMethods;
    }

    public AnnotationOrNameMatchMethodPointcut(Class<? extends Annotation> annotationType, boolean checkInherited, MappedMethod... mappedMethods) {
        this.checkInherited = checkInherited;
        this.annotationType = annotationType;
        this.mappedMethods = new HashSet<>(Arrays.asList(mappedMethods));
    }

    @SafeVarargs
    public final void setExcludedAnnotationTypes(Class<? extends Annotation>... excludedAnnotationTypes) {
        this.excludedAnnotationTypes = new HashSet<>(Arrays.asList(excludedAnnotationTypes));
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        // 排除的注解
        if (CollectionUtil.INSTANCE.isNotEmpty(excludedAnnotationTypes)) {
            for (Class<? extends Annotation> excludedAnnotationType : excludedAnnotationTypes) {
                if (matchesAnnotatedMethod(method, excludedAnnotationType, this.checkInherited)) {
                    return false;
                }
            }
        }
        // 匹配名称
        for (MappedMethod mappedMethod : this.mappedMethods) {
            if (mappedMethod.match(targetClass, method.getName())) {
                return true;
            }
        }
        // 匹配注解
        return matchesAnnotatedMethod(method, this.annotationType, this.checkInherited);
    }

    private boolean matchesAnnotatedMethod(Method method, Class<? extends Annotation> annotationType, boolean checkInherited) {
        return (checkInherited ? AnnotatedElementUtils.hasAnnotation(method, annotationType) :
                method.isAnnotationPresent(annotationType));
    }

    public static class MappedMethod {
        private final Class<?> targetClass;
        private final String methodName;

        public MappedMethod(Class<?> targetClass, String methodName) {
            this.targetClass = targetClass;
            this.methodName = methodName;
        }

        boolean match(Class<?> targetClass, String methodName) {
            return this.targetClass.isAssignableFrom(targetClass) && (this.methodName.equals(methodName) || PatternMatchUtils.simpleMatch(this.methodName, methodName));
        }
    }
}
