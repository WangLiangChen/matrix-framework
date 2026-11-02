package wang.liangchen.matrix.product.service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication(scanBasePackages = "wang.liangchen.matrix.product")
@EnableFeignClients(basePackages = "wang.liangchen.matrix.product.client")
public class ProductCatalogApplication {
    public static void main(String[] args) { SpringApplication.run(ProductCatalogApplication.class, args); }
}
