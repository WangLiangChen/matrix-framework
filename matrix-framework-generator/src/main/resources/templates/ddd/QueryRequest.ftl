package ${queryRequestPackage};

import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.pagination.Pagination;
import wang.liangchen.matrix.framework.ddd.message_pl.request.QueryRequest;
import wang.liangchen.matrix.framework.ddd.message_pl.request.IQueryRequest;

<#list entityProperties.imports as importPackage>
    import ${importPackage};
</#list>

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@QueryRequest
public class ${queryRequestClassName} extends Pagination implements IQueryRequest {

<#list entityProperties.columnMetas as columnMeta>
    /**
     * ${columnMeta.columnComment}
     */
    private ${columnMeta.modifier} ${columnMeta.fieldName};
</#list>

    public static ${queryRequestClassName} newInstance() {
        return ClassUtil.INSTANCE.instantiate(${queryRequestClassName}.class);
    }

<#list entityProperties.columnMetas as columnMeta>
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
        builder.append("${queryRequestClassName}{");
<#list entityProperties.columnMetas as columnMeta>
        builder.append("${columnMeta.fieldName} = ").append(${columnMeta.fieldName}).append(", ");
</#list>
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");
        return builder.toString();
    }
}