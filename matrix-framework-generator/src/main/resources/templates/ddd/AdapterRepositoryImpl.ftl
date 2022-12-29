package ${adapterRepositoryPackage};


import ${portRepositoryPackage}.${portRepositoryName};
import jakarta.inject.Inject;
import org.springframework.stereotype.Repository;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Repository
public class ${adapterRepositoryName} implements ${portRepositoryName} {
    private final StandaloneDao standaloneDao;

    @Inject
    public StaffRepositoryImpl(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }
}