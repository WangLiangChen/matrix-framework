package ${adapterPackage};


import ${portPackage}.${portClassName};
import jakarta.inject.Inject;
import org.springframework.stereotype.Repository;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.ddd.southbound_acl.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound_acl.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.PortType;

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Repository
@Adapter(PortType.Repository)
public class ${adapterClassName} implements ${portClassName}, IRepositoryAdapter {
    private final StandaloneDao standaloneDao;

    @Inject
    public ${adapterClassName}(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }
}