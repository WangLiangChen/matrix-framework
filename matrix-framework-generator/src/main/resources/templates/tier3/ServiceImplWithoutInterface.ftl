package ${serviceImplPackage};

import ${entityPackage}.${entityName};
import ${requestPackage}.${requestName};
import ${responsePackage}.${responseName};
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.validation.InsertGroup;
import wang.liangchen.matrix.framework.commons.validation.UpdateGroup;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.dao.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;

import java.util.Collection;
import java.util.List;
<#assign pkString=''>
<#list pkColumnMetas as columnMeta>
    <#assign pkString = pkString+ '${columnMeta.modifier} ${columnMeta.fieldName}, '>
</#list>
<#assign pkString='${pkString?substring(0,pkString?length-2)}'>

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Service
public class ${serviceImplName} {
    private final StandaloneDao standaloneDao;

    @Inject
    public ${serviceImplName}(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }

    public void insert(${requestName} request) {
        // validate fields by validator
        ValidationUtil.INSTANCE.validate(ExceptionLevel.INFO, request);
        // transform to entity
        ${entityName} entity = ${entityName}.valueOf(request);
        // TODO Assign values to fields

        // Initialize default value which value is null
        entity.initializeFields();
        this.standaloneDao.insert(entity);
    }

    public void insert(Collection<${requestName}> requests) {
        // validate empty by validator
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.INFO, requests);
        // transform to entities and process every entity
        Collection<${entityName}> entities = ${entityName}.valuesOf(requests, (source, target) -> {
            // TODO Assign values to fields

            // Initialize default value which value is null
            target.initializeFields();
        });
        this.standaloneDao.insert(entities);
    }

    public void delete(${pkString}) {
        DeleteCriteria<${entityName}> criteria = DeleteCriteria.of(${entityName}.class)
                // logically deleted
                // .markDelete()
    <#list pkColumnMetas as columnMeta>
                ._equals(${entityName}::get${columnMeta.fieldName?cap_first}, ${columnMeta.fieldName})<#if !(columnMeta_has_next)>;</#if>
    </#list>
        this.standaloneDao.delete(criteria);
    }

    public void update(${requestName} request) {
        ValidationUtil.INSTANCE.validate(ExceptionLevel.INFO, request, UpdateGroup.class);
        // validate primary key
    <#list pkColumnMetas as columnMeta>
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.INFO, request.get${columnMeta.fieldName?cap_first}());
    </#list>
        // transform to entity
        ${entityName} entity = ${entityName}.valueOf(request);
        // Two ways to set columns that force updates, such as updating to null
        // entity.addForceUpdateColumn();
        // entity.addExtendedField();
        this.standaloneDao.update(entity);
    }

    public int updateByCriteria(${requestName} request) {
        ValidationUtil.INSTANCE.notNull(request);
        // transform to entity
        ${entityName} entity = ${entityName}.newInstance();
        // TODO 将要更新的字段设置到entity
        // entity.
        UpdateCriteria<${entityName}> criteria = UpdateCriteria.of(entity);
        // 强制更新,比如将值更新为null
        // .forceUpdate()
        // TODO 构造更新条件
        return this.standaloneDao.update(criteria);
    }

    public ${responseName} select(${pkString}) {
        Criteria<${entityName}> criteria = Criteria.of(${entityName}.class)
                // Specify the returned columns
                //.resultColumns()
    <#list pkColumnMetas as columnMeta>
                ._equals(${entityName}::get${columnMeta.fieldName?cap_first}, ${columnMeta.fieldName})<#if !(columnMeta_has_next)>;</#if>
    </#list>
        ${entityName} entity = this.standaloneDao.select(criteria);
        if (null == entity) {
            return null;
        }
        //transform Entity to Response
        return entity.to(${responseName}.class);
    }

    public boolean exists(${pkString}) {
        Criteria<${entityName}> criteria = Criteria.of(${entityName}.class)
        <#list pkColumnMetas as columnMeta>
                ._equals(${entityName}::get${columnMeta.fieldName?cap_first}, ${columnMeta.fieldName})<#if !(columnMeta_has_next)>;</#if>
        </#list>
        return this.standaloneDao.exists(criteria);
    }

    public List<${responseName}> list(${requestName} request) {
        Criteria<${entityName}> criteria = Criteria.of(${entityName}.class);
        // TODO 构造查询条件
        List<${entityName}> entities = this.standaloneDao.list(criteria);
        // transform Entity to Response
        // return ObjectUtil.INSTANCE.copyProperties(entities, ${responseName}.class, (source, target) -> {});
        return ObjectUtil.INSTANCE.copyProperties(entities, ${responseName}.class);
    }

    public PaginationResult<${responseName}> pagination(${requestName} request) {
        Criteria<${entityName}> criteria = Criteria.of(${entityName}.class);
        // TODO 构造查询条件
        PaginationResult<${entityName}> entityPagination = this.standaloneDao.pagination(criteria);
        // transform Entity to Response
        // return entityPagination.to(${responseName}.class, (source, target) -> {});
        return entityPagination.to(${responseName}.class);
    }
}