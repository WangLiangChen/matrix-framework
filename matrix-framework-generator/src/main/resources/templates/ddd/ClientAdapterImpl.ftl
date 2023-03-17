package ${adapterPackage};


import ${portPackage}.${portClassName};
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.ddd.southbound_acl.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound_acl.adapter.IClientAdapter;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.PortType;

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Component
@Adapter(PortType.Client)
public class ${adapterClassName} implements ${portClassName}, IClientAdapter {
}