package io.mipangg.querymarket.domain.controller;

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
import io.mipangg.querymarket.domain.product.dto.ProductDetailResponse;
import io.mipangg.querymarket.domain.product.entity.Category;
import io.mipangg.querymarket.domain.product.controller.ProductController;
import io.mipangg.querymarket.domain.product.dto.ProductCreateRequest;
import io.mipangg.querymarket.domain.product.service.ProductService;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

        verify(productService).saveProduct(req);

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

}