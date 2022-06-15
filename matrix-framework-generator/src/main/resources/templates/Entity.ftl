package ${basePackage}.${contextPackage}.${domainPackage};

import wang.liangchen.matrix.framework.data.annotation.ColumnMarkDelete;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import javax.persistence.*;
<#list imports as importPackage>
import ${importPackage};
</#list>

/**
 * @author ${author}
 */
@Entity(name = "${tableName}")
@Table(name = "${tableName}")
public class ${entityName} extends RootEntity {
<#list columnMetas as columnMeta>
    <#if columnMeta.id>
    @Id
    </#if>
    <#if columnMeta.unique>
    @UniqueConstraint
    </#if>
    <#if columnMeta.version>
    @Version
    </#if>
    <#if columnMeta.markDeleteValue??>
    @ColumnMarkDelete("${columnMeta.markDeleteValue}")
    </#if>
    @Column(name = "${columnMeta.columnName}")
    private ${columnMeta.modifier} ${columnMeta.fieldName};

</#list>

<#list columnMetas as columnMeta>
    public ${columnMeta.modifier} get${columnMeta.fieldName?cap_first}() {
        return this.${columnMeta.fieldName};
    }
    public void set${columnMeta.fieldName?cap_first}(${columnMeta.modifier} ${columnMeta.fieldName}) {
        this.${columnMeta.fieldName} = ${columnMeta.fieldName};
    }
</#list>
}