package io.mipangg.querymarket.domain.controller;

import static io.mipangg.querymarket.TestUtils.genProductSummaryResponseSortByViewCount;
import static io.mipangg.querymarket.TestUtils.genProductSummaryResponses;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mipangg.querymarket.domain.common.PageResponse;
import io.mipangg.querymarket.domain.product.dto.ProductDetailResponse;
import io.mipangg.querymarket.domain.product.dto.ProductListRequest;
import io.mipangg.querymarket.domain.product.dto.ProductSummaryResponse;
import io.mipangg.querymarket.domain.product.entity.Category;
import io.mipangg.querymarket.domain.product.controller.ProductController;
import io.mipangg.querymarket.domain.product.dto.ProductCreateRequest;
import io.mipangg.querymarket.domain.product.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;
    
    @Test
    @DisplayName("ProductCreateRequest의 정보로 새 Product를 저장할 수 있다")
    void createProductSuccessTest() throws Exception {

        ProductCreateRequest req = new ProductCreateRequest(
                "단팥빵",
                BigDecimal.valueOf(4200),
                "seller1@example.com",
                Category.FOOD
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        verify(productService).createProduct(req);

    }

    @Test
    @DisplayName("productId로 특정 Product 정보를 삭제할 수 있다")
    void deleteProductSuccessTest() throws Exception {

        long productId = 1L;

        mockMvc.perform(delete("/api/products/{productId}", productId))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(productService).deleteProduct(productId);

    }

    @Test
    @DisplayName("productId로 특정 Product 정보를 조회할 수 있다")
    void readProductSuccessTest() throws Exception {

        long productId = 1L;
        ProductDetailResponse resp = new ProductDetailResponse(
                1L,
                "단팥빵",
                BigDecimal.valueOf(4200),
                "seller1@example.com",
                Category.FOOD,
                1
        );

        when(productService.getProduct(productId)).thenReturn(resp);

        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("단팥빵"))
                .andExpect(jsonPath("$.price").value(4200))
                .andExpect(jsonPath("$.sellerEmail").value("seller1@example.com"))
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.viewCount").value(1))
                .andDo(print());


        verify(productService).getProduct(productId);

    }

    @Test
    @DisplayName("필터링할 카테고리 정보가 포함된 요청으로 페이지네이션 된 상품 목록을 응답할 수 있다")
    void readProductsSuccessTest() throws Exception {

        List<ProductSummaryResponse> content = genProductSummaryResponses();
        Page<ProductSummaryResponse> page = new PageImpl<>(content, PageRequest.of(0, 20), 5);
        PageResponse<ProductSummaryResponse> resp = new PageResponse<>(page);

        when(productService.getProducts(any(ProductListRequest.class))).thenReturn(resp);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("단팥빵"))
                .andExpect(jsonPath("$.content[0].price").value("4200"))
                .andExpect(jsonPath("$.content[0].category").value("FOOD"))
                .andExpect(jsonPath("$.content[1].name").value("수영복"))
                .andExpect(jsonPath("$.content[1].price").value("35000"))
                .andExpect(jsonPath("$.content[1].category").value("FASHION"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andDo(print());

    }

    @Test
    @DisplayName("조회 수가 가장 높은 10개의 Product를 조회할 수 있다")
    void readPopularProductsSuccessTest() throws Exception {

        List<ProductSummaryResponse> resp = genProductSummaryResponseSortByViewCount();
        when(productService.getPopularProducts()).thenReturn(resp);

        mockMvc.perform(get("/api/products/popular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("컴퓨터"))
                .andExpect(jsonPath("$[0].price").value(52000))
                .andExpect(jsonPath("$[0].category").value("ELECTRONICS"))
                .andDo(print());

    }

}