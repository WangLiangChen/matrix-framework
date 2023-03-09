package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class CommandRequestMessagePlProperties extends NorthboundMessagePlProperties{
   private String commandRequestPackage;
   private String commandRequestClassName;

   public String getCommandRequestPackage() {
      return commandRequestPackage;
   }

   public void setCommandRequestPackage(String commandRequestPackage) {
      this.commandRequestPackage = commandRequestPackage;
   }

   public String getCommandRequestClassName() {
      return commandRequestClassName;
   }

   public void setCommandRequestClassName(String commandRequestClassName) {
      this.commandRequestClassName = commandRequestClassName;
   }
}
