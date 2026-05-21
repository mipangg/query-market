package io.mipangg.querymarket;

import io.mipangg.querymarket.domain.product.dto.ProductDetailResponse;
import io.mipangg.querymarket.domain.product.dto.ProductSummaryResponse;
import io.mipangg.querymarket.domain.product.entity.Category;
import io.mipangg.querymarket.domain.product.entity.Product;
import io.mipangg.querymarket.domain.seller.entity.Seller;
import java.math.BigDecimal;
import java.util.List;

public class TestUtils {

    public static List<Seller> genSellers() {
        return List.of(
                Seller.builder()
                        .email("seller1@example.com")
                        .build(),
                Seller.builder()
                        .email("seller2@example.com")
                        .build(),
                Seller.builder()
                        .email("seller3@example.com")
                        .build(),
                Seller.builder()
                        .email("seller4@example.com")
                        .build(),
                Seller.builder()
                        .email("seller5@example.com")
                        .build()
        );
    }

    public static List<Product> genSeller1Products() {
        Seller seller = genSellers().getFirst();
        return List.of(
                Product.builder()
                        .name("단팥빵")
                        .price(BigDecimal.valueOf(4200))
                        .seller(seller)
                        .category(Category.FOOD)
                        .build(),
                Product.builder()
                        .name("수영복")
                        .price(BigDecimal.valueOf(35000))
                        .seller(seller)
                        .category(Category.FASHION)
                        .build()
        );
    }

    public static List<Product> genProducts() {
        return List.of(
                Product.builder()
                        .name("단팥빵")
                        .price(BigDecimal.valueOf(4200))
                        .seller(genSellers().get(0))
                        .category(Category.FOOD)
                        .build(),
                Product.builder()
                        .name("수영복")
                        .price(BigDecimal.valueOf(35000))
                        .seller(genSellers().get(0))
                        .category(Category.FASHION)
                        .build(),
                Product.builder()
                        .name("의자")
                        .price(BigDecimal.valueOf(10000))
                        .seller(genSellers().get(1))
                        .category(Category.FURNITURE)
                        .build(),
                Product.builder()
                        .name("컴퓨터")
                        .price(BigDecimal.valueOf(52000))
                        .seller(genSellers().get(3))
                        .category(Category.ELECTRONICS)
                        .build(),
                Product.builder()
                        .name("맥주")
                        .price(BigDecimal.valueOf(10000))
                        .seller(genSellers().get(4))
                        .category(Category.FOOD)
                        .build()
        );
    }

    public static List<ProductSummaryResponse> genProductSummaryResponses() {
        return List.of(
                new ProductSummaryResponse(
                        1L,
                        "단팥빵",
                        BigDecimal.valueOf(4200),
                        Category.FOOD
                ),
                new ProductSummaryResponse(
                        2L,
                        "수영복",
                        BigDecimal.valueOf(35000),
                        Category.FASHION
                ),
                new ProductSummaryResponse(
                        3L,
                        "의자",
                        BigDecimal.valueOf(10000),
                        Category.FURNITURE
                ),
                new ProductSummaryResponse(
                        4L,
                        "컴퓨터",
                        BigDecimal.valueOf(52000),
                        Category.ELECTRONICS
                ),
                new ProductSummaryResponse(
                        5L,
                        "맥주",
                        BigDecimal.valueOf(10000),
                        Category.FOOD
                )
        );
    }

    public static List<ProductSummaryResponse> genProductSummaryResponseSortByPrice() {
        return List.of(
                new ProductSummaryResponse(
                        1L,
                        "컴퓨터",
                        BigDecimal.valueOf(52000),
                        Category.ELECTRONICS
                ),

                new ProductSummaryResponse(
                        2L,
                        "수영복",
                        BigDecimal.valueOf(35000),
                        Category.FASHION
                ),
                new ProductSummaryResponse(
                        3L,
                        "의자",
                        BigDecimal.valueOf(10000),
                        Category.FURNITURE
                ),
                new ProductSummaryResponse(
                        4L,
                        "맥주",
                        BigDecimal.valueOf(10000),
                        Category.FOOD
                ),
                new ProductSummaryResponse(
                        5L,
                        "단팥빵",
                        BigDecimal.valueOf(4200),
                        Category.FOOD
                )
        );
    }


    public static List<ProductSummaryResponse> genProductSummaryResponseSortByViewCount() {
        return List.of(
                new ProductSummaryResponse(
                        1L,
                        "컴퓨터",
                        BigDecimal.valueOf(52000),
                        Category.ELECTRONICS
                ),
                new ProductSummaryResponse(
                        2L,
                        "맥주",
                        BigDecimal.valueOf(10000),
                        Category.FOOD
                ),
                new ProductSummaryResponse(
                        3L,
                        "단팥빵",
                        BigDecimal.valueOf(4200),
                        Category.FOOD
                ),
                new ProductSummaryResponse(
                        4L,
                        "의자",
                        BigDecimal.valueOf(10000),
                        Category.FURNITURE
                ),
                new ProductSummaryResponse(
                        5L,
                        "수영복",
                        BigDecimal.valueOf(35000),
                        Category.FASHION
                )
        );
    }

}
