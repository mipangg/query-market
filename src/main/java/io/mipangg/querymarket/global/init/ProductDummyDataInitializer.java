package io.mipangg.querymarket.global.init;

import io.mipangg.querymarket.domain.product.entity.Category;
import io.mipangg.querymarket.domain.product.entity.Product;
import io.mipangg.querymarket.domain.seller.entity.Seller;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductDummyDataInitializer implements ApplicationRunner {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        int batchSize = 1000;

        List<Seller> sellers = new ArrayList<>();

        // Seller 100명 생성
        for (int i = 0; i < 100; i++) {

            Seller seller = Seller.builder()
                    .email(genSellerEmail(i))
                    .build();

            em.persist(seller);

            sellers.add(seller);
        }

        em.flush();
        em.clear();

        // Product 10만 개 생성
        for (int i = 1; i <= 100000; i++) {

            Seller randomSeller = sellers.get(
                    ThreadLocalRandom.current()
                            .nextInt(sellers.size())
            );

            Product product = Product.builder()
                    .name(genRandomProductName(i))
                    .price(
                            BigDecimal.valueOf(
                                    ThreadLocalRandom.current()
                                            .nextInt(1000, 100000)
                            )
                    )
                    .seller(randomSeller)
                    .category(genRandomCategory())
                    .viewCount(
                            ThreadLocalRandom.current()
                                    .nextLong(0, 100000)
                    )
                    .createdAt(
                            LocalDateTime.now()
                                    .minusDays(
                                            ThreadLocalRandom.current()
                                                    .nextInt(0, 365)
                                    )
                    )
                    .build();

            em.persist(product);

            if (i % batchSize == 0) {

                em.flush();
                em.clear();

                System.out.println(i + "개 저장 완료");
            }
        }
    }

    private String genSellerEmail(int i) {

        return "seller" + i + "@example.com";
    }

    private Category genRandomCategory() {

        Category[] values = Category.values();

        return values[
                ThreadLocalRandom.current()
                        .nextInt(values.length)
                ];
    }

    private String genRandomProductName(int i) {

        String[] keywords = {
                "맥북",
                "커피",
                "원두",
                "텀블러",
                "키보드",
                "마우스",
                "모니터"
        };

        String keyword = keywords[
                ThreadLocalRandom.current()
                        .nextInt(keywords.length)
                ];

        return keyword + " 상품 " + i;
    }
}
