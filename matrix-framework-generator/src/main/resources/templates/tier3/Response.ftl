package ${responsePackage};

import wang.liangchen.matrix.framework.commons.type.ClassUtil;

/**
* @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
public class ${responseName} {
    public static ${responseName} newInstance() {
        return ClassUtil.INSTANCE.instantiate(${responseName}.class);
    }

}