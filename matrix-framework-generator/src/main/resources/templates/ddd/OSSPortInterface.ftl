package ${portPackage};

import wang.liangchen.matrix.framework.ddd.southbound_acl.port.IOSSPort;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.PortType;

/**
 * @author ${author} ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Port(PortType.OSS)
public interface ${portClassName} extends IOSSPort {
}