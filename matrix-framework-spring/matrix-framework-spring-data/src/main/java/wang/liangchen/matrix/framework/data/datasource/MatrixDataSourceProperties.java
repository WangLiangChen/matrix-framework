package wang.liangchen.matrix.framework.data.datasource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class MatrixDataSourceProperties extends DataSourceProperties {
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
