package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 品牌数据访问对象：Spring Data JPA仓储，仅在南向适配层内部使用。
 */
public interface BrandDao extends JpaRepository<BrandPo, String> {

    List<BrandPo> findAllByOrderByNameAsc();
}
