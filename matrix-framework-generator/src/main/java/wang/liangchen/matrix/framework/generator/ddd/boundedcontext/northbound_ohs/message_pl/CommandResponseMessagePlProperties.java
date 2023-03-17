package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.EntityProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class CommandResponseMessagePlProperties extends NorthboundMessagePlProperties {
    private String commandResponsePackage;
    private String commandResponseClassName;
    private EntityProperties entityProperties;

    public String getCommandResponsePackage() {
        return commandResponsePackage;
    }

    public void setCommandResponsePackage(String commandResponsePackage) {
        this.commandResponsePackage = commandResponsePackage;
    }

    public String getCommandResponseClassName() {
        return commandResponseClassName;
    }

    public void setCommandResponseClassName(String commandResponseClassName) {
        this.commandResponseClassName = commandResponseClassName;
    }

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }
}
