package ${responsePackage};

import wang.liangchen.matrix.framework.commons.type.ClassUtil;

import java.time.LocalDateTime;

/**
* @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
public class ${responseName} {

<#list columnMetas as columnMeta>
    /**
     * ${columnMeta.columnComment}
     */
    private ${columnMeta.modifier} ${columnMeta.fieldName};
</#list>

    public static ${responseName} newInstance() {
        return ClassUtil.INSTANCE.instantiate(${responseName}.class);
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
        builder.append("${responseName}{");
<#list columnMetas as columnMeta>
        builder.append("${columnMeta.fieldName} = ").append(${columnMeta.fieldName}).append(", ");
</#list>
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");
        return builder.toString();
    }
}