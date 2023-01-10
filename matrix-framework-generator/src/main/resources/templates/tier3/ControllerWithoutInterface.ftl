package ${controllerPackage};

import ${serviceImplPackage}.${serviceImplName};
import ${requestPackage}.${requestName};
import ${responsePackage}.${responseName};
import ${javaPackage}.inject.Inject;
import org.springframework.web.bind.annotation.*;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;

import java.util.List;
<#assign pkString=''>
<#assign pkParameterString=''>
<#list pkColumnMetas as columnMeta>
    <#assign pkString = pkString+ '@RequestParam ${columnMeta.modifier} ${columnMeta.fieldName}, '>
    <#assign pkParameterString = pkParameterString+ '${columnMeta.fieldName}, '>
</#list>
<#assign pkString='${pkString?substring(0,pkString?length-2)}'>
<#assign pkParameterString='${pkParameterString?substring(0,pkParameterString?length-2)}'>

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@RestController
@RequestMapping("/${tableName}")
public class ${controllerName} {
    private final ${serviceImplName} service;

    @Inject
    public ${controllerName}(${serviceImplName} service) {
        this.service = service;
    }

    @PostMapping("/insert")
    public void insert(@RequestBody ${requestName} request) {
        service.insert(request);
    }

    @GetMapping("/delete")
    public void delete(${pkString}) {
        service.delete(${pkParameterString});
    }

    @PostMapping("/update")
    public void update(@RequestBody ${requestName} request) {
        service.update(request);
    }

    @PostMapping("/updateByCriteria")
    public void updateByCriteria(@RequestBody ${requestName} request) {
        service.updateByCriteria(request);
    }

    @GetMapping("/select")
    public ${responseName} select(${pkString}) {
        return service.select(${pkParameterString});
    }

    @PostMapping("/list")
    public List<${responseName}> list(@RequestBody ${requestName} request) {
        return service.list(request);
    }

    @PostMapping("/pagination")
    public PaginationResult<${responseName}> pagination(@RequestBody ${requestName} request) {
        return service.pagination(request); 
    }
}