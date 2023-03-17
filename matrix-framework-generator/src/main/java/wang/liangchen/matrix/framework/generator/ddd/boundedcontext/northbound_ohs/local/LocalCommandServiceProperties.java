package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.local;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.EntityProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.ManagerProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl.CommandRequestMessagePlProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl.CommandResponseMessagePlProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class LocalCommandServiceProperties extends LocalProperties {
    private String commandServicePackage;
    private String commandServiceClassName;

    private EntityProperties entityProperties;
    private ManagerProperties managerProperties;
    private CommandRequestMessagePlProperties commandRequestMessagePlProperties;
    private CommandResponseMessagePlProperties commandResponseMessagePlProperties;

    public String getCommandServicePackage() {
        return commandServicePackage;
    }

    public void setCommandServicePackage(String commandServicePackage) {
        this.commandServicePackage = commandServicePackage;
    }

    public String getCommandServiceClassName() {
        return commandServiceClassName;
    }

    public void setCommandServiceClassName(String commandServiceClassName) {
        this.commandServiceClassName = commandServiceClassName;
    }

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }

    public ManagerProperties getManagerProperties() {
        return managerProperties;
    }

    public void setManagerProperties(ManagerProperties managerProperties) {
        this.managerProperties = managerProperties;
    }

    public CommandRequestMessagePlProperties getCommandRequestMessagePlProperties() {
        return commandRequestMessagePlProperties;
    }

    public void setCommandRequestMessagePlProperties(CommandRequestMessagePlProperties commandRequestMessagePlProperties) {
        this.commandRequestMessagePlProperties = commandRequestMessagePlProperties;
    }

    public CommandResponseMessagePlProperties getCommandResponseMessagePlProperties() {
        return commandResponseMessagePlProperties;
    }

    public void setCommandResponseMessagePlProperties(CommandResponseMessagePlProperties commandResponseMessagePlProperties) {
        this.commandResponseMessagePlProperties = commandResponseMessagePlProperties;
    }
}
