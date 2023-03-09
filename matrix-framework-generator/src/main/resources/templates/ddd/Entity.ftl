package ${entityPackage};
<#if columnStateUseConstantEnum>
import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;
</#if>
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.annotation.ColumnMarkDelete;
import wang.liangchen.matrix.framework.data.annotation.ColumnJson;
import wang.liangchen.matrix.framework.data.annotation.ColumnState;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
<#if aggregateRoot>
import wang.liangchen.matrix.framework.ddd.domain.AggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.IAggregateRoot;
<#else>
import wang.liangchen.matrix.framework.ddd.domain.IEntity;
</#if>

import jakarta.persistence.*;
<#list imports as importPackage>
import ${importPackage};
</#list>

/**
 * ${tableComment!}
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
<#if aggregateRoot>
@AggregateRoot
</#if>
@Entity(name = "${tableName}")
public class ${entityName} extends RootEntity implements <#if aggregateRoot>IAggregateRoot<#else>IEntity</#if> {
<#list columnMetas as columnMeta>
    /**
     * ${columnMeta.columnComment}
    <#if columnMeta.markDeleteValue??>
     * 逻辑删除列和值
    </#if>
    <#if columnMeta.json>
     * 对象和JSON格式自动互转列
     * 非基本类型需实现Serializable接口以避免代码错误提示
    </#if>
    <#if columnMeta.state>
     * 状态列
    </#if>
    <#if columnMeta.version>
     * 版本列
     * 更新和删除时,非空则启用乐观锁
    </#if>
     */
    <#if columnMeta.id>
    @Id
    @IdStrategy(IdStrategy.Strategy.MatrixFlake)
    </#if>
    <#if columnMeta.version>
    @Version
    </#if>
    <#if columnMeta.markDeleteValue??>
    @ColumnMarkDelete("${columnMeta.markDeleteValue}")
    </#if>
    <#if columnMeta.json>
    @ColumnJson
    </#if>
    <#if columnMeta.state>
    @ColumnState
    </#if>
    <#-- @Column(name = "${columnMeta.columnName}") -->
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