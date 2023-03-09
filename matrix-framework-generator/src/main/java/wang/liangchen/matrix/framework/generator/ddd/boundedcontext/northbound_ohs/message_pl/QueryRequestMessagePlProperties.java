package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class QueryRequestMessagePlProperties extends NorthboundMessagePlProperties{
   private String queryRequestPackage;
   private String queryRequestClassName;

   public String getQueryRequestPackage() {
      return queryRequestPackage;
   }

   public void setQueryRequestPackage(String queryRequestPackage) {
      this.queryRequestPackage = queryRequestPackage;
   }

   public String getQueryRequestClassName() {
      return queryRequestClassName;
   }

   public void setQueryRequestClassName(String queryRequestClassName) {
      this.queryRequestClassName = queryRequestClassName;
   }
}
