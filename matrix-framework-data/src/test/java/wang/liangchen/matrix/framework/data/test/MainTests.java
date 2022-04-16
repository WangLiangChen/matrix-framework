package wang.liangchen.matrix.framework.data.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.data.dao.criteria.CriteriaBuilder;
import wang.liangchen.matrix.framework.data.dao.criteria.SqlValue;
import wang.liangchen.matrix.framework.data.dao.criteria.WhereSql;

public class MainTests {
    @Test
    public void testBuilder() {
        CriteriaBuilder<Staff> builder = CriteriaBuilder.<Staff>newInstance()
                .equals(Staff::getStaff_name, SqlValue.of(Staff::getStaff_id))
                .equals(Staff::getStaff_id, SqlValue.of("string"))
                .equals(Staff::getStaff_name,SqlValue.of(123));
        WhereSql whereSql = builder.whereSql();
        System.out.println(whereSql.getSql());
        System.out.println(whereSql.getValues());
    }
}
