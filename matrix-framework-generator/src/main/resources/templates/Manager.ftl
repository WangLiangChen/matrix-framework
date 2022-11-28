package ${basePackage}.${contextPackage}.${domainPackage}.${aggregatePackage};

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.DomainService;
import wang.liangchen.matrix.framework.ddd.domain.IDomainService;
import ${basePackage}.${contextPackage}.${southboundAclPackage}.${portPackage}.repository.${repositoryClassName};

import javax.inject.Inject;


/**
 * @author ${author}
 */
@Service
@DomainService
public class ${managerClassName} implements IDomainService {
    private final ${repositoryClassName} repository;

    @Inject
    public ${managerClassName}(${repositoryClassName} repository) {
        this.repository = repository;
    }

}