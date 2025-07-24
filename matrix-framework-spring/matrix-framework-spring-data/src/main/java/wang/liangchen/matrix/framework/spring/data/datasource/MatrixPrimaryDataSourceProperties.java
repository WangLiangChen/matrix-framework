package wang.liangchen.matrix.framework.spring.data.datasource;


public class MatrixPrimaryDataSourceProperties extends MatrixDataSourceProperties {
    private MatrixMultiDataSourceProperties matrix;

    public MatrixMultiDataSourceProperties getMatrix() {
        return matrix;
    }

    public void setMatrix(MatrixMultiDataSourceProperties matrix) {
        this.matrix = matrix;
    }
}
