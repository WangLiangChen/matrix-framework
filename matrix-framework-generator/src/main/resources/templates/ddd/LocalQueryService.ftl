package ${queryServicePackage};

import ${entityProperties.entityPackage}.${entityProperties.entityName};
import ${managerProperties.managerPackage}.${managerProperties.managerClassName};
import ${queryRequestMessagePlProperties.queryRequestPackage}.${queryRequestMessagePlProperties.queryRequestClassName};
import ${queryResponseMessagePlProperties.queryResponsePackage}.${queryResponseMessagePlProperties.queryResponseClassName};
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.validation.InsertGroup;
import wang.liangchen.matrix.framework.commons.validation.UpdateGroup;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.dao.criteria.EntityGetter;
import wang.liangchen.matrix.framework.data.dao.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;
import wang.liangchen.matrix.framework.ddd.northbound_ohs.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound_ohs.local.IApplicationService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
<#assign pkString=''>
<#assign pkFields=''>
<#list entityProperties.pkColumnMetas as columnMeta>
    <#assign pkString = pkString+ '${columnMeta.modifier} ${columnMeta.fieldName}, '>
    <#assign pkFields = pkFields+ '${columnMeta.fieldName}, '>
</#list>
<#assign pkString='${pkString?substring(0,pkString?length-2)}'>
<#assign pkFields='${pkFields?substring(0,pkFields?length-2)}'>
/**
* @author Liangchen.Wang 2023-03-12 10:00
*/
@Service
@ApplicationService
public class ${queryServiceClassName} implements IApplicationService {
    private final ${managerProperties.managerClassName} manager;

    @Inject
    public ${queryServiceClassName}(${managerProperties.managerClassName} manager) {
        this.manager = manager;
    }

    public ${queryResponseMessagePlProperties.queryResponseClassName} find(${pkString}) {
        EntityGetter<${entityProperties.entityName}>[] resultFields = new EntityGetter[0];
        Optional<${entityProperties.entityName}> optional = this.manager.find(${pkFields}, resultFields);
        return optional.map(entity -> entity.to(${queryResponseMessagePlProperties.queryResponseClassName}.class)).orElse(null);
    }

    public List<${queryResponseMessagePlProperties.queryResponseClassName}> find(${queryRequestMessagePlProperties.queryRequestClassName} request) {
        Criteria<${entityProperties.entityName}> criteria = Criteria.of(${entityProperties.entityName}.class);
        List<${entityProperties.entityName}> entities = this.manager.find(criteria);
        return ObjectUtil.INSTANCE.copyProperties(entities, ${queryResponseMessagePlProperties.queryResponseClassName}.class);
    }

    public PaginationResult<${queryResponseMessagePlProperties.queryResponseClassName}> pagination(${queryRequestMessagePlProperties.queryRequestClassName} request) {
        Criteria<${entityProperties.entityName}> criteria = Criteria.of(${entityProperties.entityName}.class);
        PaginationResult<${entityProperties.entityName}> pagination = this.manager.pagination(criteria);
        return pagination.to(${queryResponseMessagePlProperties.queryResponseClassName}.class);
    }
<#if entityProperties.stateColumnMeta??>
    public List<${entityProperties.entityName}> byStates(String... states) {
        return this.manager.byStates(states);
    }
</#if>
}