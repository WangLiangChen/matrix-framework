package ${basePackage}.${subPackage}.${domainPackage};

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.DomainService;
import wang.liangchen.matrix.framework.ddd.domain.RootDomainService;

/**
 * @author ${author}
 */
@Service
@DomainService
public class ${entityName}DomainService extends RootDomainService<${entityName}> {

}