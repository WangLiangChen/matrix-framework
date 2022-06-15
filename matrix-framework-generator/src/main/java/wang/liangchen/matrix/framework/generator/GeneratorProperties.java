package wang.liangchen.matrix.framework.generator;

/**
 * @author Liangchen.Wang 2022-04-27 10:16
 */
public class GeneratorProperties {
    private String author;
    private String output;
    private String basePackage;
    private String contextPackage;

    private String datasource;
    private String tableName;
    private String entityName;
    private String columnVersion;
    private String columnMarkDelete;
    private String columnMarkDeleteValue;
    private boolean camelCase;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

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

    public String getContextPackage() {
        return contextPackage;
    }

    public void setContextPackage(String contextPackage) {
        this.contextPackage = contextPackage;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
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
}
