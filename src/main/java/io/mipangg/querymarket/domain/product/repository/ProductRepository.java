package io.mipangg.querymarket.domain.product.repository;

import io.mipangg.querymarket.domain.product.dto.ProductDetailResponse;
import io.mipangg.querymarket.domain.product.entity.Category;
import io.mipangg.querymarket.domain.product.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(long id);

    @Query("""
              select new io.mipangg.querymarket.domain.product.dto.ProductDetailResponse(
                  p.name, p.price, s.email, p.category, p.viewCount
              )
              from Product p
              join p.seller s
              where (:category is null or p.category = :category)
            """)
    Page<ProductDetailResponse> findProductDtos(
            @Param("category") Category category,
            Pageable pageable
    );

    @Query("select p from Product p join fetch p.seller order by p.viewCount desc")
    List<Product> findPopularProducts(Pageable pageable);
}
