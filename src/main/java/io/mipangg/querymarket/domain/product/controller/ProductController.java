package io.mipangg.querymarket.domain.product.controller;

import io.mipangg.querymarket.domain.common.CursorPageResponse;
import io.mipangg.querymarket.domain.common.ProductPageResponse;
import io.mipangg.querymarket.domain.product.dto.ProductCreateRequest;
import io.mipangg.querymarket.domain.product.dto.ProductDetailResponse;
import io.mipangg.querymarket.domain.product.dto.ProductListRequest;
import io.mipangg.querymarket.domain.product.dto.ProductSearchRequest;
import io.mipangg.querymarket.domain.product.dto.ProductSummaryResponse;
import io.mipangg.querymarket.domain.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(
            @RequestBody @Valid ProductCreateRequest req
    ) {

        productService.createProduct(req);

    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @PathVariable @Positive long productId
    ) {

        productService.deleteProduct(productId);

    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDetailResponse getProduct(
            @PathVariable @Positive long productId
    ) {

        return productService.getProduct(productId);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductPageResponse getProducts(
            @Valid ProductListRequest req
        ) {

        return productService.getProducts(req);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductSummaryResponse> getPopularProducts() {

        return productService.getPopularProducts();
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ProductPageResponse searchProducts(
            @Valid ProductSearchRequest req
    ) {
        return productService.searchProducts(req);
    }

}
