package ${portPackage};

import ${entityProperties.entityPackage}.${entityProperties.entityName};
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.dao.criteria.EntityGetter;
import wang.liangchen.matrix.framework.data.dao.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.Port;
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
@Port(PortType.Repository)
public interface ${portClassName} extends IRepositoryPort {

    int create(${entityProperties.entityName} entity);

    int create(Collection<${entityProperties.entityName}> entities);

    int delete(${pkString});

    int delete(DeleteCriteria<${entityProperties.entityName}> criteria);

    int update(${entityProperties.entityName} entity);

    int update(UpdateCriteria<${entityProperties.entityName}> criteria);

    Optional<${entityProperties.entityName}> find(${pkString}, EntityGetter<${entityProperties.entityName}>... resultFields);

    List<${entityProperties.entityName}> find(Criteria<${entityProperties.entityName}> criteria);

    PaginationResult<${entityProperties.entityName}> pagination(Criteria<${entityProperties.entityName}> criteria);

<#if entityProperties.stateColumnMeta??>
    int stateTransfer(${pkString}, ${entityProperties.stateColumnMeta.modifier} to, ${entityProperties.stateColumnMeta.modifier}... from);

    List<${entityProperties.entityName}> byStates(${entityProperties.stateColumnMeta.modifier}... states);
</#if>
}