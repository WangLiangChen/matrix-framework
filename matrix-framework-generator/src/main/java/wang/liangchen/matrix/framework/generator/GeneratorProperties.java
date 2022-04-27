package wang.liangchen.matrix.framework.generator;

import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;

import java.util.List;

/**
 * @author Liangchen.Wang 2022-04-27 10:16
 */
public class GeneratorProperties {
    private String output;
    private String basePackage;

    private String tableName;
    private String className;
    private String subPackage;
    private String columnVersion;
    private String columnMarkDelete;
    private String columnMarkDeleteValue;
    private boolean camelCase;

    private List<ColumnMeta> columnMetas;

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubPackage() {
        return subPackage;
    }

    public void setSubPackage(String subPackage) {
        this.subPackage = subPackage;
    }

    public String getColumnVersion() {
        return columnVersion;
    }

    public void setColumnVersion(String columnVersion) {
        this.columnVersion = columnVersion;
    }

    public String getColumnMarkDelete() {
        return columnMarkDelete;
    }

    public void setColumnMarkDelete(String columnMarkDelete) {
        this.columnMarkDelete = columnMarkDelete;
    }

    public String getColumnMarkDeleteValue() {
        return columnMarkDeleteValue;
    }

    public void setColumnMarkDeleteValue(String columnMarkDeleteValue) {
        this.columnMarkDeleteValue = columnMarkDeleteValue;
    }

    public boolean isCamelCase() {
        return camelCase;
    }

    public void setCamelCase(boolean camelCase) {
        this.camelCase = camelCase;
    }

    public List<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    public void setColumnMetas(List<ColumnMeta> columnMetas) {
        this.columnMetas = columnMetas;
    }
}
