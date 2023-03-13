package ${commandResponsePackage};

import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.ddd.message_pl.response.IResponse;
import wang.liangchen.matrix.framework.ddd.message_pl.response.Response;

<#list entityProperties.imports as importPackage>
    import ${importPackage};
</#list>

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Response
public class ${commandResponseClassName} implements IResponse {

<#list entityProperties.columnMetas as columnMeta>
    /**
     * ${columnMeta.columnComment}
     */
    private ${columnMeta.modifier} ${columnMeta.fieldName};
</#list>

    public static ${commandResponseClassName} newInstance() {
        return ClassUtil.INSTANCE.instantiate(${commandResponseClassName}.class);
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
        builder.append("${commandResponseClassName}{");
<#list entityProperties.columnMetas as columnMeta>
        builder.append("${columnMeta.fieldName} = ").append(${columnMeta.fieldName}).append(", ");
</#list>
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");
        return builder.toString();
    }
}