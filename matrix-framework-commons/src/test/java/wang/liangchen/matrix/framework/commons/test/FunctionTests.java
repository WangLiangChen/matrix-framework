package wang.liangchen.matrix.framework.commons.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.configuration.ConfigurationResolver;
import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.commons.function.SerializableConsumer;

/**
 * @author Liangchen.Wang 2022-04-15 10:25
 */
public class FunctionTests {
    @Test
    public void testMethod() {
        consumer(Bean::getBean_name);
    }

    private void consumer(SerializableConsumer<Bean> consumer) {
        String methodName = LambdaUtil.INSTANCE.getReferencedFieldName(consumer);
        System.out.println(methodName);
    }
}
