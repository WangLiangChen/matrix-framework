package wang.liangchen.matrix.shop.product.southbound.adapter;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 商品上下文JPA配置：限定Spring Data JPA仓储与实体的扫描范围，
 * 商品与订单两个上下文合并为单体应用时互不干扰。
 */
@Configuration
@EnableJpaRepositories(basePackages = "wang.liangchen.matrix.shop.product.southbound.adapter.repository")
@EntityScan(basePackages = "wang.liangchen.matrix.shop.product.southbound.adapter.repository")
public class ProductJpaConfiguration {
}
