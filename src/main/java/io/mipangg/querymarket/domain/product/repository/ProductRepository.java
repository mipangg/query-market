package io.mipangg.querymarket.domain.product.repository;

import io.mipangg.querymarket.domain.product.dto.ProductSummaryResponse;
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

    @Query("select p from Product p join fetch p.seller where p.id = :id")
    Optional<Product> findById(@Param("id") long id);

    @Query("""
              select new io.mipangg.querymarket.domain.product.dto.ProductSummaryResponse(
                  p.id, p.name, p.price, p.category
              )
              from Product p
              where (:category is null or p.category = :category)
            """)
    Page<ProductSummaryResponse> findProducts(
            @Param("category") Category category,
            Pageable pageable
    );

    @Query("""
              select new io.mipangg.querymarket.domain.product.dto.ProductSummaryResponse(
                  p.id, p.name, p.price, p.category
              )
              from Product p
              where (:category is null or p.category = :category)
              and (:cursor is null or p.id < :cursor)
              order by p.id desc
            """)
    List<ProductSummaryResponse> findProductsByCursor(
            @Param("category") Category category,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    @Query("select p from Product p order by p.viewCount desc")
    List<Product> findPopularProducts(Pageable pageable);

    @Query(
            value = """
                    select *
                    from product p
                    where match(p.name)
                                against(:keyword in natural language mode)
                    """,
            countQuery = """
                    select count(*)
                    from product p
                    where match(p.name)
                          against(:keyword in natural language mode)
                    """,
            nativeQuery = true
    )
    Page<Product> searchProductsByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(
            value = """
                    select *
                    from product p
                    where match(p.name)
                                against(:keyword in natural language mode)
                    and (:cursor is null or p.id < :cursor)
                    order by p.id desc
                    """,
            nativeQuery = true
    )
    List<Product> searchProductsByKeywordWithCursor(
            @Param("keyword") String keyword,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
}
