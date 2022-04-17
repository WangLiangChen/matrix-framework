package wang.liangchen.matrix.framework.data.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.data.dao.criteria.*;

public class MainTests {
    @Test
    public void testBuilder() {
        Criteria<Staff> builder = Criteria.of(Staff.class).equals(Staff::getStaff_id, SqlValue.of(Staff::getStaff_id))
                .equals(Staff::getStaff_name, SqlValue.of("abc"))
                .OR(SubCriteria.of(Staff.class).equals(Staff::getStaff_id, SqlValue.of(456L))
                        .equals(Staff::getStaff_name, SqlValue.of("def"))
                        .AND(SubCriteria.of(Staff.class).equals(Staff::getStaff_id, SqlValue.of(789L))
                                .equals(Staff::getStaff_name, SqlValue.of("ghi"))))
                .resultColumns(Staff::getStaff_id, Staff::getStaff_name);
        ;

        WhereSql resolve = CriteriaResolver.INSTANCE.resolve(builder);
        System.out.println(resolve.getSql());
        System.out.println(resolve.getValues());
        System.out.println(resolve.getResultColumns());

    }
}
