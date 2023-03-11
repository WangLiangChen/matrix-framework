package ${managerPackage};

import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.dao.criteria.EntityGetter;
import wang.liangchen.matrix.framework.data.dao.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;
import wang.liangchen.matrix.framework.ddd.domain.DomainService;
import wang.liangchen.matrix.framework.ddd.domain.IDomainService;
<#list portsProperties as portProperties>
import ${portProperties.portPackage}.${portProperties.portClassName};
</#list>

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
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Service
@DomainService
public class ${managerClassName} implements IDomainService {
<#assign params=''>
<#list portsProperties as portProperties>
    private final ${portProperties.portClassName} ${portProperties.portType?lower_case};
    <#assign params = params+ '${portProperties.portClassName} ${portProperties.portType?lower_case}, '>
</#list>
<#assign params='${params?substring(0,params?length-2)}'>
    @Inject
    public StaffManager(${params}) {
<#list portsProperties as portProperties>
        this.${portProperties.portType?lower_case} = ${portProperties.portType?lower_case};
</#list>
    }

    public int create(${entityProperties.entityName} entity) {
        return this.repository.create(entity);
    }

    public int create(Collection<${entityProperties.entityName}> entities) {
        return this.repository.create(entities);
    }

    public int delete(${pkString}) {
        return this.repository.delete(${pkFields});
    }

    public int delete(DeleteCriteria<${entityProperties.entityName}> criteria) {
        return this.repository.delete(criteria);
    }

    public int update(${entityProperties.entityName} entity) {
        return this.repository.update(entity);
    }

    public int update(UpdateCriteria<${entityProperties.entityName}> criteria) {
        return this.repository.update(criteria);
    }

    public Optional<${entityProperties.entityName}> find(${pkString}, EntityGetter<${entityProperties.entityName}>... resultFields) {
        return this.repository.find(${pkFields}, resultFields);
    }

    public List<${entityProperties.entityName}> find(Criteria<${entityProperties.entityName}> criteria) {
        return this.repository.find(criteria);
    }

    public PaginationResult<${entityProperties.entityName}> pagination(Criteria<${entityProperties.entityName}> criteria) {
        return this.repository.pagination(criteria);
    }

<#if entityProperties.stateColumnMeta??>
    public int stateTransfer(${pkString}, ${entityProperties.stateColumnMeta.modifier} to, ${entityProperties.stateColumnMeta.modifier}... from) {
        return this.repository.stateTransfer(${pkFields}, to,from);
    }

    public List<${entityProperties.entityName}> byStates(${entityProperties.stateColumnMeta.modifier}... states) {
        return this.repository.byStates(states);
    }
</#if>
}