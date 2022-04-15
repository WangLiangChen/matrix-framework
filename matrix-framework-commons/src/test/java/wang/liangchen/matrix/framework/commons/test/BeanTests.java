package wang.liangchen.matrix.framework.commons.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.object.BeanUtil;

/**
 * @author Liangchen.Wang 2022-04-15 11:42
 */
public class BeanTests {

    @Test
    public void testResolveFieldName() {
        String getter = "getName";
        String fieldName = BeanUtil.INSTANCE.resolveFieldName(getter);
        Assert.INSTANCE.isTrue("name".equals(fieldName), "no match");
        String setter = "setName";
        fieldName = BeanUtil.INSTANCE.resolveFieldName(setter);
        Assert.INSTANCE.isTrue("name".equals(fieldName), "no match");
        String is = "isName";
        fieldName = BeanUtil.INSTANCE.resolveFieldName(is);
        Assert.INSTANCE.isTrue("name".equals(fieldName), "no match");
    }
    @Test
    public void testResolveMethodName(){
        String field="name";
        String methodName = BeanUtil.INSTANCE.resolveGetterName(field);
        Assert.INSTANCE.equals("getName",methodName,"no match");
        methodName = BeanUtil.INSTANCE.resolveSetterName(field);
        Assert.INSTANCE.equals("setName",methodName,"no match");
        methodName = BeanUtil.INSTANCE.resolveIsName(field);
        Assert.INSTANCE.equals("isName",methodName,"no match");
    }
}
