package ${servicePackage};

import ${requestPackage}.${requestName};
import ${responsePackage}.${responseName};
<#assign pkString=''>
<#list pkColumnMetas as columnMeta>
 <#assign pkString = pkString+ '${columnMeta.modifier} ${columnMeta.fieldName}, '>
</#list>
<#assign pkString='${pkString?substring(0,pkString?length-2)}'>

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
public interface ${serviceName} {
    void insert(${requestName} request);

    ${responseName} select(${pkString});
}