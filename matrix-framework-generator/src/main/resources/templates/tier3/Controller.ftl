package ${controllerPackage};

import ${servicePackage}.${serviceName};
import jakarta.inject.Inject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@RestController
@RequestMapping("/${tableName}")
public class ${controllerName} {
    private final ${serviceName} service;

    @Inject
    public ${controllerName}(${serviceName} service) {
        this.service = service;
    }
}