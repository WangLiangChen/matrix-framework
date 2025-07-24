package wang.liangchen.matrix.framework.data.datasource;

import java.util.Map;

public class MatrixMultiDataSourceProperties {
    private Map<String, MatrixDataSourceProperties> datasources;

    public Map<String, MatrixDataSourceProperties> getDatasources() {
        return datasources;
    }

    public void setDatasources(Map<String, MatrixDataSourceProperties> datasources) {
        this.datasources = datasources;
    }
}
