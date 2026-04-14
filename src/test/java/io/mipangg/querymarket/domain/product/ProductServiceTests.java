package io.mipangg.querymarket.domain.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.querymarket.domain.seller.Seller;
import io.mipangg.querymarket.domain.seller.SellerService;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SellerService sellerService;

    @Test
    @DisplayName("새 상품을 등록할 수 있다")
    void saveProductSuccessTest() {

        ProductCreateRequest resq = new ProductCreateRequest(
                "단팥빵",
                BigDecimal.valueOf(4200),
                "seller1@example.com",
                Category.FOOD
        );

        Seller seller = Seller.builder()
                .email("seller1@example.com")
                .build();

        when(sellerService.getOrCreateSeller(anyString())).thenReturn(seller);

        productService.saveProduct(resq);

        verify(productRepository).save(any(Product.class));

    }

}