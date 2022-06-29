package ${basePackage}.${contextPackage}.${domainPackage};

import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
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

    public static ${entityName} valueOf(Object source) {
        return ObjectUtil.INSTANCE.copyProperties(source, ${entityName}.class);
    }

    public static ${entityName} newInstance() {
        return ClassUtil.INSTANCE.instantiate(${entityName}.class);
    }

<#list columnMetas as columnMeta>
    public ${columnMeta.modifier} get${columnMeta.fieldName?cap_first}() {
        return this.${columnMeta.fieldName};
    }
    public void set${columnMeta.fieldName?cap_first}(${columnMeta.modifier} ${columnMeta.fieldName}) {
        this.${columnMeta.fieldName} = ${columnMeta.fieldName};
    }
</#list>

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("${entityName}{");
<#list columnMetas as columnMeta>
        builder.append("${columnMeta.fieldName} = ").append(${columnMeta.fieldName}).append(", ");
</#list>
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");
        return builder.toString();
    }
}