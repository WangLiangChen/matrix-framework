package ${portPackage};

import wang.liangchen.matrix.framework.ddd.southbound_acl.port.IFilePort;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.PortType;

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Port(PortType.File)
public interface ${portClassName} extends IFilePort {
}