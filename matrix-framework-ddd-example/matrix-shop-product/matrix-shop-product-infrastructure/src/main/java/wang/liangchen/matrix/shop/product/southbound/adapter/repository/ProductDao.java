package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 商品数据访问对象：Spring Data JPA仓储，仅在南向适配层内部使用。
 */
public interface ProductDao extends JpaRepository<ProductPo, String> {

    @Query("select p from ProductPo p where p.listed = true "
            + "and (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%')) "
            + "or lower(p.subtitle) like lower(concat('%', :keyword, '%'))) "
            + "and (:categoryId is null or p.categoryId = :categoryId)")
    List<ProductPo> searchListed(@Param("keyword") String keyword, @Param("categoryId") String categoryId);
}
