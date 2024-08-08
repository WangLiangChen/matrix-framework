package wang.liangchen.matrix.framework.commons.test;

import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

public class StringUtilTest {
    public static void main(String[] args) {
        String s = StringUtil.INSTANCE.format("{},{},{d},{5}","a","b","c","d","e","f");
        System.out.println(s);
    }
}
