package io.mipangg.querymarket.domain.product;

import static io.mipangg.querymarket.TestUtils.genProductSummaryResponseSortByPrice;
import static io.mipangg.querymarket.TestUtils.genProducts;
import static io.mipangg.querymarket.TestUtils.genSellers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.querymarket.domain.common.PageResponse;
import io.mipangg.querymarket.domain.product.dto.ProductDetailResponse;
import io.mipangg.querymarket.domain.product.dto.ProductListRequest;
import io.mipangg.querymarket.domain.product.dto.ProductSummaryResponse;
import io.mipangg.querymarket.domain.product.entity.Category;
import io.mipangg.querymarket.domain.product.entity.Product;
import io.mipangg.querymarket.domain.product.dto.ProductCreateRequest;
import io.mipangg.querymarket.domain.product.repository.ProductRepository;
import io.mipangg.querymarket.domain.product.service.ProductService;
import io.mipangg.querymarket.domain.seller.entity.Seller;
import io.mipangg.querymarket.domain.seller.service.SellerService;
import io.mipangg.querymarket.global.exception.CustomLogicException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

        Seller seller = genSellers().getFirst();

        when(sellerService.getOrCreateSeller(anyString())).thenReturn(seller);

        productService.createProduct(resq);

        verify(productRepository).save(any(Product.class));

    }

    @Test
    @DisplayName("productId로 특정 상품을 삭제할 수 있다")
    void deleteProductSuccessTest() {

        long productId = 1L;
        Product product = genProducts().getFirst();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository).findById(anyLong());
        verify(productRepository).delete(any(Product.class));


    }

    @Test
    @DisplayName("존재하지 않는 상품 삭제를 시도하면 예외가 발생한다")
    void deleteProductFailTest() {

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> {
                    productService.deleteProduct(1L);
                }
        ).isInstanceOf(CustomLogicException.class)
                .hasMessage("데이터를 찾을 수 없습니다.");

    }

    @Test
    @DisplayName("productId로 특정 상품을 조회할 수 있고 조회 수는 업데이트된다")
    void getProductSuccessTest() {

        long productId = 1L;
        Product product = genProducts().getFirst();
        ProductDetailResponse expected = new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getSeller().getEmail(),
                product.getCategory(),
                product.getViewCount() + 1
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDetailResponse actual = productService.getProduct(productId);

        assertThat(actual.name()).isEqualTo(expected.name());
        assertThat(actual.price()).isEqualTo(expected.price());
        assertThat(actual.sellerEmail()).isEqualTo(expected.sellerEmail());
        assertThat(actual.category()).isEqualTo(expected.category());
        assertThat(actual.viewCount()).isEqualTo(expected.viewCount());

        verify(productRepository).findById(productId);

    }

    @Test
    @DisplayName("존재하지 않은 상품 조회 시 예외가 발생한다")
    void getProductFailTest() {

        long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> {
                    productService.getProduct(productId);
                }
        ).isInstanceOf(CustomLogicException.class)
                .hasMessage("데이터를 찾을 수 없습니다.");

    }
    
    @Test
    @DisplayName("전체 상품 조회 시 가격순으로 정렬할 수 있다")
    void getProductsSuccessTest() {

        ProductListRequest req = new ProductListRequest(0, 10, "price", null);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("price").ascending());

        List<ProductSummaryResponse> content = genProductSummaryResponseSortByPrice();
        Page<ProductSummaryResponse> page =
                new PageImpl<>(content, PageRequest.of(1, 10), content.size());

        when(productRepository.findProductDtos(req.category(), pageable)).thenReturn(page);

        PageResponse<ProductSummaryResponse> resp = productService.getProducts(req);

        assertThat(resp.getContent()).hasSize(content.size());
        ProductSummaryResponse firstProduct = resp.getContent().getFirst();
        assertThat(firstProduct.name()).isEqualTo("컴퓨터");
        assertThat(firstProduct.price()).isEqualTo(BigDecimal.valueOf(52000));
        assertThat(firstProduct.category()).isEqualTo(Category.ELECTRONICS);
    }

    @Test
    @DisplayName("조회수가 가장 높은 10개의 상품 조회를 할 수 있다")
    void getPopularProductsSuccessTest() {

        List<Product> products = genProducts();

        when(productRepository.findPopularProducts(PageRequest.of(0, 10))).thenReturn(products);

        List<ProductSummaryResponse> result = productService.getPopularProducts();

        assertThat(result).hasSize(products.size());
        assertThat(result.get(0).name()).isEqualTo("단팥빵");
        assertThat(result.get(0).price()).isEqualTo(BigDecimal.valueOf(4200));
        assertThat(result.get(1).name()).isEqualTo("수영복");
        assertThat(result.get(1).price()).isEqualTo(BigDecimal.valueOf(35000));
        assertThat(result.get(2).name()).isEqualTo("의자");
        assertThat(result.get(2).price()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(result.get(3).name()).isEqualTo("컴퓨터");
        assertThat(result.get(3).price()).isEqualTo(BigDecimal.valueOf(52000));
        assertThat(result.get(4).name()).isEqualTo("맥주");
        assertThat(result.get(4).price()).isEqualTo(BigDecimal.valueOf(10000));

    }

    @Test
    @DisplayName("등록된 상품이 없다면 인기 상품 조회 시 빈 리스트가 반환된다")
    void getPopularProductsEmptySuccessTest() {

        when(productRepository.findPopularProducts(PageRequest.of(0, 10)))
                .thenReturn(Collections.emptyList());

        List<ProductSummaryResponse> result = productService.getPopularProducts();

        assertThat(result).isEmpty();

    }

}