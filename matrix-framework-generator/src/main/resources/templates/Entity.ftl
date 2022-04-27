package ${basePackage}.${subPackage};

import wang.liangchen.matrix.framework.data.annotation.ColumnMarkDelete;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import javax.persistence.*;


@Entity(name = "${tableName}")
@Table(name = "${tableName}")
public class ${entityName} extends RootEntity {
<#list columnMetas as columnMeta>
    <#if columnMeta.id>
    @Id
    </#if>
    <#if columnMeta.unique>
    @@UniqueConstraint
    </#if>
    <#if columnMeta.version>
    @Version
    </#if>
    <#if columnMeta.markDeleteValue??>
    @ColumnDelete("${columnMeta.markDeleteValue}")
    </#if>
    @Column(name = "${columnMeta.columnName}")
    private ${columnMeta.fieldClassName} ${columnMeta.fieldName}
</#list>

<#list columnMetas as columnMeta>
    public ${columnMeta.fieldClassName} get${columnMeta.fieldName?cap_first}() { return this.${columnMeta.fieldName}; }
    public void set${columnMeta.fieldName?cap_first}(${columnMeta.fieldClassName} ${columnMeta.fieldName}) { this.${columnMeta.fieldName} = ${columnMeta.fieldName}; }
</#list>
}