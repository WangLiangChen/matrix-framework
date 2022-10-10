package ${basePackage}.${contextPackage}.${domainPackage}.${aggregatePackage};

import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.annotation.ColumnMarkDelete;
import wang.liangchen.matrix.framework.data.annotation.ColumnJson;
import wang.liangchen.matrix.framework.data.annotation.ColumnState;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
<#if aggregateRoot>
import wang.liangchen.matrix.framework.ddd.domain.AggregateRoot;
</#if>

import javax.persistence.*;
<#list imports as importPackage>
import ${importPackage};
</#list>

/**
 * @author ${author}
 */
<#if aggregateRoot>
@AggregateRoot
</#if>
@Entity(name = "${tableName}")
public class ${entityName} extends RootEntity {
<#list columnMetas as columnMeta>
    <#if columnMeta.id>
    @Id
    @IdStrategy(IdStrategy.Strategy.MatrixFlake)
    </#if>
    <#if columnMeta.unique>
    @UniqueConstraint
    </#if>
    <#if columnMeta.version>
    @Version
    </#if>
    <#if columnMeta.markDeleteValue??>
    /**
     * 逻辑删除的列和逻辑值
     */
    @ColumnMarkDelete("${columnMeta.markDeleteValue}")
    </#if>
    <#if columnMeta.json>
    /**
     * 对象和JSON格式互转列
     */
    @ColumnJson
    </#if>
    <#if columnMeta.state>
    /**
     * 状态列
     */
    @ColumnState
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
    public static ${entityName} newInstance(boolean initializeFields) {
        ${entityName} entity = ClassUtil.INSTANCE.instantiate(${entityName}.class);
        if(initializeFields) {
            entity.initializeFields();
        }
        return entity;
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