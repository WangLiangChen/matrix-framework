package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 属性数据访问对象：Spring Data JPA仓储，仅在南向适配层内部使用。
 */
public interface AttributeDao extends JpaRepository<AttributePo, String> {

    List<AttributePo> findAllByOrderByNameAsc();
}
