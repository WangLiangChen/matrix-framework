package wang.liangchen.matrix.shop.product.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商品微服务引导类：以商品限界上下文为边界的独立微服务入口。
 */
@SpringBootApplication(scanBasePackages = "wang.liangchen.matrix.shop.product")
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
