package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.local;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.NorthboundOhsProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class LocalProperties extends NorthboundOhsProperties {
    private String localPackage;

    public String getLocalPackage() {
        return localPackage;
    }

    public void setLocalPackage(String localPackage) {
        this.localPackage = localPackage;
    }

}
