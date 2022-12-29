package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.message_pl;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.SouthboundAclProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class SouthboundMessagePlProperties extends SouthboundAclProperties {
    private String messagePlPackage;

    public void setMessagePlPackage(String messagePlPackage) {
        this.messagePlPackage = messagePlPackage;
    }

    public String getMessagePlPackage() {
        return messagePlPackage;
    }
}
