package wang.liangchen.matrix.framework.generator;

/**
 * @author Liangchen.Wang 2022-04-27 10:16
 */
public class GeneratorProperties {
    private String author;
    private String output;
    private String basePackage;
    private String contextPackage;
    private String aggregatePackage;
    private boolean aggregateRoot;

    private String datasource;
    private String tableName;
    private String tableComment;
    private String entityName;
    private String columnVersion;
    private String columnState;
    private boolean columnStateUseConstantEnum;
    private String columnJson;
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

    public String getAggregatePackage() {
        return aggregatePackage;
    }

    public void setAggregatePackage(String aggregatePackage) {
        this.aggregatePackage = aggregatePackage;
    }

    public boolean isAggregateRoot() {
        return aggregateRoot;
    }

    public void setAggregateRoot(boolean aggregateRoot) {
        this.aggregateRoot = aggregateRoot;
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

    public String getColumnJson() {
        return columnJson;
    }

    public void setColumnJson(String columnJson) {
        this.columnJson = columnJson;
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

    public void setColumnState(String columnState) {
        this.columnState = columnState;
    }

    public String getColumnState() {
        return columnState;
    }

    public void setColumnStateUseConstantEnum(boolean columnStateUseConstantEnum) {
        this.columnStateUseConstantEnum = columnStateUseConstantEnum;
    }

    public boolean isColumnStateUseConstantEnum() {
        return columnStateUseConstantEnum;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getTableComment() {
        return tableComment;
    }
}
