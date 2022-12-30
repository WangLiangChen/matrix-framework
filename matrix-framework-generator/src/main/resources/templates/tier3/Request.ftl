package ${requestPackage};

import ${entityPackage}.${entityName};
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.pagination.Pagination;

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
public class ${requestName} extends Pagination {

    public static ${requestName} newInstance() {
        return ClassUtil.INSTANCE.instantiate(${requestName}.class);
    }

    public ${entityName} toEntity() {
        return ObjectUtil.INSTANCE.copyProperties(this, ${entityName}.class);
    }
}