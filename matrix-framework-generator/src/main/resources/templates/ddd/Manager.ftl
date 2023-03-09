package ${managerPackage};

import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.DomainService;
import wang.liangchen.matrix.framework.ddd.domain.IDomainService;
import ${repositoryProperties.portRepositoryPackage}.${repositoryProperties.portRepositoryName};
import ${clientProperties.portClientPackage}.${clientProperties.portClientName};
import ${publisherProperties.portPublisherPackage}.${publisherProperties.portPublisherName};

/**
 * @author ${author}
 */
@Service
@DomainService
public class ${managerClassName} implements IDomainService {
    private final ${repositoryProperties.portRepositoryName} repository;
    private final ${clientProperties.portClientName} client;
    private final ${publisherProperties.portPublisherName} publisher;

    @Inject
    public StaffManager(${repositoryProperties.portRepositoryName} repository, ${clientProperties.portClientName} client, ${publisherProperties.portPublisherName} publisher) {
        this.repository = repository;
        this.client = client;
        this.publisher = publisher;
    }

}