package ${serviceImplPackage};

import ${entityPackage}.${entityName};
import ${servicePackage}.${serviceName};
import ${requestPackage}.${requestName};
import ${responsePackage}.${responseName};
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
<#assign pkString=''>
<#list pkColumnMetas as columnMeta>
    <#assign pkString = pkString+ '${columnMeta.modifier} ${columnMeta.fieldName}, '>
</#list>
<#assign pkString='${pkString?substring(0,pkString?length-2)}'>

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Service
public class ${serviceImplName} implements ${serviceName} {
    private final StandaloneDao standaloneDao;

    @Inject
    public ${serviceImplName}(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }
    @Override
    public void insert(${requestName} request) {
        ValidationUtil.INSTANCE.validate(ExceptionLevel.INFO, request);
        // transform Request to Entity
        ${entityName} entity = request.toEntity();
        this.standaloneDao.insert(entity);
    }

    @Override
    public ${responseName} select(${pkString}) {
        Criteria<${entityName}> criteria = Criteria.of(${entityName}.class)
                // Specify the returned columns
                //.resultColumns()
<#list pkColumnMetas as columnMeta>
                ._equals(${entityName}::get${columnMeta.fieldName?cap_first}, ${columnMeta.fieldName})
</#list>;
        ${entityName} entity = this.standaloneDao.select(criteria);
        //transform Entity to Response
        return entity.to(${responseName}.class);
    }
}
