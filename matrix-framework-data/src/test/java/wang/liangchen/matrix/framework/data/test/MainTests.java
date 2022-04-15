package wang.liangchen.matrix.framework.data.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.data.query.CriteriaBuilder;

public class MainTests {
    @Test
    public void testBuilder() {
        CriteriaBuilder<Staff> builder = CriteriaBuilder.<Staff>newInstance()
                .eq(Staff::getStaff_name, "wanglc");
        System.out.println(builder);
    }
}
