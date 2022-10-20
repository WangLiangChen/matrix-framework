package ${basePackage}.${contextPackage}.${domainPackage}.${aggregatePackage};

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.ddd.domain.DomainService;

import javax.inject.Inject;
import java.util.List;


/**
 * @author ${author}
 */
@Service
@DomainService
public class ${managerClassName} {
    private final StandaloneDao repository;

    @Inject
    public ${managerClassName}(StandaloneDao repository) {
        this.repository = repository;
    }

    public int add(${entityName} entity) {
        return repository.insert(entity);
    }
<#assign pkString=''>
<#list pkColumnMetas as columnMeta>
    <#assign pkString = pkString+ '${columnMeta.modifier} ${columnMeta.fieldName}, '>
</#list>
<#assign pkString='${pkString?substring(0,pkString?length-2)}'>

    public int delete(${pkString}) {
        ${entityName} entity = ${entityName}.newInstance();
<#list pkColumnMetas as columnMeta>
        entity.set${columnMeta.fieldName?cap_first}(${columnMeta.fieldName});
</#list>
        return repository.delete(entity);
    }

    public int update(${entityName} entity) {
        return repository.update(entity);
    }

    public ${entityName} byKey(${pkString}, String... resultColumns) {
        return repository.select(Criteria.of(${entityName}.class)
                .resultColumns(resultColumns)
<#list pkColumnMetas as columnMeta>
                ._equals(${entityName}::get${columnMeta.fieldName?cap_first}, ${columnMeta.fieldName})
</#list>
                );
    }

<#if stateColumnMeta??>
    public void stateTransition(${pkString}, ${stateColumnMeta.modifier} to, ${stateColumnMeta.modifier}... from) {
        ${entityName} entity = ${entityName}.newInstance();
        entity.set${stateColumnMeta.fieldName?cap_first}(to);
        UpdateCriteria<${entityName}> updateCriteria = UpdateCriteria.of(entity)
    <#list pkColumnMetas as columnMeta>
                ._equals(${entityName}::get${columnMeta.fieldName?cap_first}, ${columnMeta.fieldName})
    </#list>
                ._in(${entityName}::get${stateColumnMeta.fieldName?cap_first}, from);
        repository.update(updateCriteria);
    }

    public List<${entityName}> byStates(${stateColumnMeta.modifier}... states) {
        return repository.list(Criteria.of(${entityName}.class)._in(${entityName}::get${stateColumnMeta.fieldName?cap_first}, states));
    }
</#if>
}