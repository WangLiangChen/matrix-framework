package wang.liangchen.matrix.framework.ddd.assembler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark an assembler
 * 装配器：消息契约模型与领域对象之间的相互转换（入站装配为领域对象可兼任工厂，出站装配为消息契约）。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Assembler {
}
