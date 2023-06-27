package wang.liangchen.matrix.framework.data.commons.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

/**
 * @author Liangchen.Wang 2023-03-26 12:20
 */
@AutoConfigureAfter(StandaloneDao.class)
public class DataCommonsAutoConfiguration {
}
