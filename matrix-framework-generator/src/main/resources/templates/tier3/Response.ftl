package ${responsePackage};

import ${entityPackage}.${entityName};
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;

/**
* @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
public class ${responseName} {
    public static ${responseName} newInstance() {
        return ClassUtil.INSTANCE.instantiate(${responseName}.class);
    }

    public static ${responseName} fromEntity(${entityName} entity) {
        return ObjectUtil.INSTANCE.copyProperties(entity, StaffResponse.class);
    }
}