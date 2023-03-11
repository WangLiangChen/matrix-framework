package ${adapterPackage};


import ${entityProperties.entityPackage}.${entityProperties.entityName};
import ${portPackage}.${portClassName};
import jakarta.inject.Inject;
import org.springframework.stereotype.Repository;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.dao.criteria.EntityGetter;
import wang.liangchen.matrix.framework.data.dao.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;
import wang.liangchen.matrix.framework.ddd.southbound_acl.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound_acl.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.PortType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
<#assign pkString=''>
<#list entityProperties.pkColumnMetas as columnMeta>
    <#assign pkString = pkString+ '${columnMeta.modifier} ${columnMeta.fieldName}, '>
</#list>
<#assign pkString='${pkString?substring(0,pkString?length-2)}'>
/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Repository
@Adapter(PortType.Repository)
public class ${adapterClassName} implements ${portClassName}, IRepositoryAdapter {
    private final StandaloneDao standaloneDao;

    @Inject
    public ${adapterClassName}(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }

    @Override
    public int create(${entityProperties.entityName} entity) {
        return this.standaloneDao.insert(entity);
    }

    @Override
    public int create(Collection<${entityProperties.entityName}> entities) {
        return this.standaloneDao.insert(entities);
    }

    @Override
    public int delete(${pkString}) {
        DeleteCriteria<${entityProperties.entityName}> criteria = DeleteCriteria.of(${entityProperties.entityName}.class)
<#list entityProperties.pkColumnMetas as columnMeta>
                ._equals(${entityProperties.entityName}::get${columnMeta.fieldName?cap_first}, ${columnMeta.fieldName})<#if !columnMeta_has_next>;</#if>
</#list>
        return this.standaloneDao.delete(criteria);
    }

    @Override
    public int delete(DeleteCriteria<${entityProperties.entityName}> criteria) {
        return this.standaloneDao.delete(criteria);
    }

    @Override
    public int update(${entityProperties.entityName} entity) {
        return this.standaloneDao.update(entity);
    }

    @Override
    public int update(UpdateCriteria<${entityProperties.entityName}> criteria) {
        return this.standaloneDao.update(criteria);
    }

    @Override
    public Optional<${entityProperties.entityName}> find(${pkString}, EntityGetter<${entityProperties.entityName}>... resultFields) {
        Criteria<${entityProperties.entityName}> criteria = Criteria.of(${entityProperties.entityName}.class)
                .resultFields(resultFields)
<#list entityProperties.pkColumnMetas as columnMeta>
                ._equals(${entityProperties.entityName}::get${columnMeta.fieldName?cap_first}, ${columnMeta.fieldName})<#if !columnMeta_has_next>;</#if>
</#list>
         return Optional.ofNullable(this.standaloneDao.select(criteria));
    }

    @Override
    public List<${entityProperties.entityName}> find(Criteria<${entityProperties.entityName}> criteria) {
        return this.standaloneDao.list(criteria);
    }

    @Override
    public PaginationResult<${entityProperties.entityName}> pagination(Criteria<${entityProperties.entityName}> criteria) {
        return this.standaloneDao.pagination(criteria);
    }

<#if entityProperties.stateColumnMeta??>
    @Override
    public int stateTransfer(${pkString}, ${entityProperties.stateColumnMeta.modifier} to, ${entityProperties.stateColumnMeta.modifier}... from) {
        ${entityProperties.entityName} entity = ${entityProperties.entityName}.newInstance();
        entity.set${entityProperties.stateColumnMeta.fieldName?cap_first}(to);
        UpdateCriteria<${entityProperties.entityName}> updateCriteria = UpdateCriteria.of(entity)
    <#list entityProperties.pkColumnMetas as columnMeta>
                ._equals(${entityProperties.entityName}::get${columnMeta.fieldName?cap_first}, ${columnMeta.fieldName})
    </#list>
                ._in(${entityProperties.entityName}::get${entityProperties.stateColumnMeta.fieldName?cap_first}, from);
        return this.standaloneDao.update(updateCriteria);
    }

    @Override
    public List<${entityProperties.entityName}> byStates(${entityProperties.stateColumnMeta.modifier}... states) {
        return this.standaloneDao.list(Criteria.of(${entityProperties.entityName}.class)._in(${entityProperties.entityName}::get${entityProperties.stateColumnMeta.fieldName?cap_first}, states));
    }
</#if>
}