package ${basePackage}.${contextPackage}.${southboundAclPackage}.${portPackage}.repository;


import ${basePackage}.${contextPackage}.${domainPackage}.${aggregatePackage}.${entityName};

/**
 * @author ${author}
 */
public interface ${repositoryClassName} {
    void put(${entityName} entity);
<#assign pkString=''>
<#list pkColumnMetas as columnMeta>
 <#assign pkString = pkString+ '${columnMeta.modifier} ${columnMeta.fieldName}, '>
</#list>
<#assign pkString='${pkString?substring(0,pkString?length-2)}'>
    ${entityName} take(${pkString});
}