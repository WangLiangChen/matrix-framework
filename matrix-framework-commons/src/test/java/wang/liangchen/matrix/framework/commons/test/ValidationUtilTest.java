package wang.liangchen.matrix.framework.commons.test;

import jakarta.validation.constraints.NotNull;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

public class ValidationUtilTest {
    public static void main(String[] args) {
        String s = ValidationUtil.INSTANCE.resolveMessage("你好{jakarta.validation.constraints.NotNull.message}代码","xxx");
        System.out.println(s);
        class Person{
            @NotNull
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
        Person person = new Person();
        ValidationUtil.INSTANCE.validate(person);
    }
}
