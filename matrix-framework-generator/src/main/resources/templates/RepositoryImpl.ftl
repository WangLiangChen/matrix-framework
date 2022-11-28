package ${basePackage}.${contextPackage}.${southboundAclPackage}.${adapterPackage}.repository;

import org.springframework.stereotype.Repository;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import ${basePackage}.${contextPackage}.${domainPackage}.${aggregatePackage}.${entityName};
import ${basePackage}.${contextPackage}.${southboundAclPackage}.${portPackage}.repository.${repositoryClassName};

import javax.inject.Inject;

/**
 * @author ${author}
 */
@Repository
public class ${repositoryImplClassName} implements ${repositoryClassName} {
    private StandaloneDao standaloneDao;

    @Inject
    public ${repositoryImplClassName}(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }
    @Override
    public void put(${entityName} entity) {

    }
<#assign pkString=''>
<#list pkColumnMetas as columnMeta>
    <#assign pkString = pkString+ '${columnMeta.modifier} ${columnMeta.fieldName}, '>
</#list>
<#assign pkString='${pkString?substring(0,pkString?length-2)}'>

    @Override
    public ${entityName} take(${pkString}) {
        return null;
    }
}