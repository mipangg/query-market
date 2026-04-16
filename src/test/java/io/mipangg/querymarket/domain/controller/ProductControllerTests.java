package io.mipangg.querymarket.domain.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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

        ProductCreateRequest resq = new ProductCreateRequest(
                "단팥빵",
                BigDecimal.valueOf(4200),
                "seller1@example.com",
                Category.FOOD
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resq))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        verify(productService).saveProduct(resq);

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

}