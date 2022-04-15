package wang.liangchen.matrix.framework.commons.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.exception.Assert;

/**
 * @author Liangchen.Wang 2022-04-15 13:46
 */
public class MainTests {
    @Test
    public void testAssertEquals(){
        String a="a";
        String b="a";
        Assert.INSTANCE.equals(a,b,"not match");
    }
    @Test
    public void testAssertNotEquals(){
        String a=null;
        String b=null;
        Assert.INSTANCE.notEquals(a,b,"not match");
    }
}
