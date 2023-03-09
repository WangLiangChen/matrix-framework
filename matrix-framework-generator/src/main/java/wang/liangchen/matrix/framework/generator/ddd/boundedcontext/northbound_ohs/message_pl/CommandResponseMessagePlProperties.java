package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class CommandResponseMessagePlProperties extends NorthboundMessagePlProperties {
    private String commandResponsePackage;
    private String commandResponseClassName;

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
}
