package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain;

import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-12-28 19:01
 */
public class EntityProperties extends AggregateProperties{
    private String entityPackage;
    private String tableName;
    private String tableComment;
    private String entityName;
    private String columnVersion;
    private String columnState;
    private boolean columnStateUseConstantEnum;
    private String columnJson;
    private String columnMarkDelete;
    private String columnMarkDeleteValue;
    private Boolean aggregateRoot;

    private Set<String> imports = new HashSet<>();
    private List<ColumnMeta> columnMetas = new ArrayList<>();
    private List<ColumnMeta> pkColumnMetas = new ArrayList<>();
    private ColumnMeta stateColumnMeta;

    public String getEntityPackage() {
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
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

    public String getColumnState() {
        return columnState;
    }

    public void setColumnState(String columnState) {
        this.columnState = columnState;
    }

    public boolean isColumnStateUseConstantEnum() {
        return columnStateUseConstantEnum;
    }

    public void setColumnStateUseConstantEnum(boolean columnStateUseConstantEnum) {
        this.columnStateUseConstantEnum = columnStateUseConstantEnum;
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

    public Boolean getAggregateRoot() {
        return aggregateRoot;
    }

    public void setAggregateRoot(Boolean aggregateRoot) {
        this.aggregateRoot = aggregateRoot;
    }


    public Set<String> getImports() {
        return imports;
    }

    public void setImports(Set<String> imports) {
        this.imports = imports;
    }

    public List<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    public void setColumnMetas(List<ColumnMeta> columnMetas) {
        this.columnMetas = columnMetas;
    }

    public List<ColumnMeta> getPkColumnMetas() {
        return pkColumnMetas;
    }

    public void setPkColumnMetas(List<ColumnMeta> pkColumnMetas) {
        this.pkColumnMetas = pkColumnMetas;
    }

    public ColumnMeta getStateColumnMeta() {
        return stateColumnMeta;
    }

    public void setStateColumnMeta(ColumnMeta stateColumnMeta) {
        this.stateColumnMeta = stateColumnMeta;
    }
}
