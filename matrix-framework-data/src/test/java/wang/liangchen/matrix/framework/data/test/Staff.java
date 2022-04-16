package wang.liangchen.matrix.framework.data.test;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Liangchen.Wang 2022-04-15 22:18
 */
@Entity(name = "staff")
public class Staff {
    @Id
    private Long staff_id;
    private String staff_name;

    public Long getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(Long staff_id) {
        this.staff_id = staff_id;
    }

    public String getStaff_name() {
        return staff_name;
    }

    public void setStaff_name(String staff_name) {
        this.staff_name = staff_name;
    }
}
