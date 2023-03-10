package ${managerPackage};

import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.DomainService;
import wang.liangchen.matrix.framework.ddd.domain.IDomainService;
<#list portsProperties as portProperties>
import ${portProperties.portPackage}.${portProperties.portClassName};
</#list>


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

}