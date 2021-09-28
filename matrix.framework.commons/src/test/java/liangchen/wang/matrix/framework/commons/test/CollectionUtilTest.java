package liangchen.wang.matrix.framework.commons.test;

import liangchen.wang.matrix.framework.commons.utils.PrintUtil;
import org.junit.jupiter.api.Test;

public class CollectionUtilTest {
    @Test
    public void test(){
        PrintUtil.INSTANCE.buffer("a");
        PrintUtil.INSTANCE.buffer("b");
        PrintUtil.INSTANCE.buffer("c");
        PrintUtil.INSTANCE.buffer("d");
        PrintUtil.INSTANCE.buffer("e");
        PrintUtil.INSTANCE.prettyPrint();
    }
}
