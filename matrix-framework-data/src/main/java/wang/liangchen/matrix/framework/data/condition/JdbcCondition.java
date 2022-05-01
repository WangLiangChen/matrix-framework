package wang.liangchen.matrix.framework.data.condition;


import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import wang.liangchen.matrix.framework.data.enumeration.DataStatus;

import java.lang.annotation.*;

/**
 * @author LiangChen.Wang 2019/9/17 20:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(JdbcCondition.InnerJdbcCondition.class)
public @interface JdbcCondition {
    class InnerJdbcCondition implements ConfigurationCondition {
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            return DataStatus.INSTANCE.isJdbcEnabled();
        }

        @Override
        public ConfigurationPhase getConfigurationPhase() {
            /**
             * 强制在注册阶段处理条件，而不是在解析阶段,解决条件判断在Import之前的问题
             * 解析阶段--shouldSkip---Import--注册阶段--shouldSkip
             */
            return ConfigurationPhase.REGISTER_BEAN;
        }
    }
}
