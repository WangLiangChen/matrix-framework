package wang.liangchen.matrix.framework.commons.test;

import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;

public class StringUtilTest {
    public static void main(String[] args) throws InterruptedException {

    }
    class A extends ConstantEnum{
        public final static ConstantEnum A = new ConstantEnum("a","a");
        public A(String key, String value) {
            super(key, value);
        }
    }
}
