package ${commandServicePackage};

import ${entityProperties.entityPackage}.${entityProperties.entityName};
import ${managerProperties.managerPackage}.${managerProperties.managerClassName};
import ${commandRequestMessagePlProperties.commandRequestPackage}.${commandRequestMessagePlProperties.commandRequestClassName};
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.commons.validation.InsertGroup;
import wang.liangchen.matrix.framework.commons.validation.UpdateGroup;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.dao.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.ddd.northbound_ohs.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound_ohs.local.IApplicationService;

import java.util.Collection;
<#assign pkString=''>
<#assign pkFields=''>
<#list entityProperties.pkColumnMetas as columnMeta>
    <#assign pkString = pkString+ '${columnMeta.modifier} ${columnMeta.fieldName}, '>
    <#assign pkFields = pkFields+ '${columnMeta.fieldName}, '>
</#list>
<#assign pkString='${pkString?substring(0,pkString?length-2)}'>
<#assign pkFields='${pkFields?substring(0,pkFields?length-2)}'>
/**
* @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
@Service
@ApplicationService
public class ${commandServiceClassName} implements IApplicationService {
    private final ${managerProperties.managerClassName} manager;

    @Inject
    public ${commandServiceClassName}(${managerProperties.managerClassName} manager) {
        this.manager = manager;
    }

    public int create(${commandRequestMessagePlProperties.commandRequestClassName} request) {
        ValidationUtil.INSTANCE.validate(request, InsertGroup.class);
        ${entityProperties.entityName} entity = ${entityProperties.entityName}.valueOf(request);
        return this.manager.create(entity);
    }

    public int create(Collection<${commandRequestMessagePlProperties.commandRequestClassName}> requests) {
        Collection<${entityProperties.entityName}> entities = ${entityProperties.entityName}.valuesOf(requests, ${entityProperties.entityName}.class);
        return this.manager.create(entities);
    }

    public int delete(${pkString}) {
        return this.manager.delete(${pkFields});
    }

    public int delete(${commandRequestMessagePlProperties.commandRequestClassName} request) {
        DeleteCriteria<${entityProperties.entityName}> criteria = DeleteCriteria.of(${entityProperties.entityName}.class);
        return this.manager.delete(criteria);
    }

    public int update(${commandRequestMessagePlProperties.commandRequestClassName} request) {
        ValidationUtil.INSTANCE.validate(request, UpdateGroup.class);
        ${entityProperties.entityName} entity = ${entityProperties.entityName}.valueOf(request);
        return this.manager.update(entity);
    }

    public int updateMatching(${commandRequestMessagePlProperties.commandRequestClassName} request) {
        ValidationUtil.INSTANCE.validate(request, UpdateGroup.class);
        UpdateCriteria<${entityProperties.entityName}> criteria = UpdateCriteria.of(${entityProperties.entityName}.class);
        return this.manager.update(criteria);
    }
}