package wang.liangchen.matrix.framework.generator;

/**
 * @author Liangchen.Wang 2022-12-28 22:30
 */
public class DomainProperties extends BoundedContextProperties{
    private String domainPackage;

    public String getDomainPackage() {
        return domainPackage;
    }

    public void setDomainPackage(String domainPackage) {
        this.domainPackage = domainPackage;
    }
}
