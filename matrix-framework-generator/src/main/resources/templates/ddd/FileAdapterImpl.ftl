package ${adapterPackage};


import ${portPackage}.${portClassName};
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.ddd.southbound_acl.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound_acl.adapter.IFileAdapter;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.PortType;

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Component
@Adapter(PortType.Publisher)
public class ${adapterClassName} implements ${portClassName}, IFileAdapter {
}