package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.NorthboundOhsProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class NorthboundMessagePlProperties extends NorthboundOhsProperties{
    private String messagePlPackage;

    public void setMessagePlPackage(String messagePlPackage) {
        this.messagePlPackage = messagePlPackage;
    }

    public String getMessagePlPackage() {
        return messagePlPackage;
    }
}
