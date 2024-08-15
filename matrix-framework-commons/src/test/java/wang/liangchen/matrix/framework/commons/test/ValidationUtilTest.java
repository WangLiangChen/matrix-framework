package wang.liangchen.matrix.framework.commons.test;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

public class ValidationUtilTest {
    public static void main(String[] args) {
        String s = ValidationUtil.INSTANCE.resolveMessage("你好{Parameter.NotNull}代码","xxx");
        System.out.println(s);
    }
}
