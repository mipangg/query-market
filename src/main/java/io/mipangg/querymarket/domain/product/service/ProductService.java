package io.mipangg.querymarket.domain.product.service;

import io.mipangg.querymarket.domain.common.CursorPageResponse;
import io.mipangg.querymarket.domain.common.PageResponse;
import io.mipangg.querymarket.domain.common.ProductPageResponse;
import io.mipangg.querymarket.domain.product.dto.ProductCreateRequest;
import io.mipangg.querymarket.domain.product.dto.ProductDetailResponse;
import io.mipangg.querymarket.domain.product.dto.ProductListRequest;
import io.mipangg.querymarket.domain.product.dto.ProductSearchRequest;
import io.mipangg.querymarket.domain.product.dto.ProductSummaryResponse;
import io.mipangg.querymarket.domain.product.repository.ProductRepository;
import io.mipangg.querymarket.domain.product.entity.Product;
import io.mipangg.querymarket.domain.seller.entity.Seller;
import io.mipangg.querymarket.domain.seller.service.SellerService;
import io.mipangg.querymarket.global.exception.CustomLogicException;
import io.mipangg.querymarket.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final SellerService sellerService;

    @Transactional
    public void createProduct(ProductCreateRequest req) {
        Seller seller = sellerService.getOrCreateSeller(req.sellerEmail());

        productRepository.save(
                Product.builder()
                        .name(req.name())
                        .price(req.price())
                        .seller(seller)
                        .category(req.category())
                        .build()
        );
    }

    @Transactional
    public void deleteProduct(long productId) {
        Product target = productRepository.findById(productId)
                .orElseThrow(() -> new CustomLogicException(
                        ErrorCode.NOT_FOUND,
                        "상품을 찾을 수 없습니다."
                ));

        productRepository.delete(target);
    }

    @Transactional
    public ProductDetailResponse getProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomLogicException(
                        ErrorCode.NOT_FOUND,
                        "상품을 찾을 수 없습니다."
                ));

        product.updateViewCount();

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getSeller().getEmail(),
                product.getCategory(),
                product.getViewCount()
        );
    }

    @Transactional(readOnly = true)
    public ProductPageResponse getProducts(ProductListRequest req) {

        if (req.sort().equals("latest")) {
            return getProductsByCursor(req);
        }

        return getProductsByOffset(req);
    }

    @Cacheable(
            value = "popular-products",
            key = "'top10'"
    )
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getPopularProducts() {

        log.info("DB 조회 발생");

        List<Product> products = productRepository.findPopularProducts(PageRequest.of(0, 10));

        List<ProductSummaryResponse> resps = new ArrayList<>();
        for (Product product : products) {
            resps.add(
                    new ProductSummaryResponse(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getCategory()
                    )
            );
        }
        return resps;
    }

    @Transactional(readOnly = true)
    public ProductPageResponse searchProducts(ProductSearchRequest req) {
        if (req.sort().equals("latest")) {
            return searchProductsByCursor(req);
        }

        return searchProductsByOffset(req);
    }

    private Pageable createPageable(Integer page, Integer size, String sort) {
        if (sort.equals("price")) {
            return PageRequest.of(page, size, Sort.by("price").ascending());
        } else { // views
            return PageRequest.of(page, size, Sort.by("viewCount").descending());
        }
    }


    private PageResponse getProductsByOffset (ProductListRequest req) {
        Pageable pageable = createPageable(req.page(), req.size(), req.sort());

        Page<ProductSummaryResponse> products =
                productRepository.findProducts(req.category(), pageable);

        return new PageResponse<>(products);

    }

    private CursorPageResponse getProductsByCursor(ProductListRequest req) {
        Pageable pageable = PageRequest.of(0, req.size() + 1);

        List<ProductSummaryResponse> products =
                productRepository.findProductsByCursor(
                        req.category(),
                        req.cursor(),
                        pageable
                );

        boolean hasNext = products.size() > req.size();

        if (hasNext) {
            products.remove(req.size());
        }

        Long nextCursor = null;

        if (!products.isEmpty()) {
            nextCursor = products.get(products.size() - 1).id();
        }

        return new CursorPageResponse<>(products, nextCursor, hasNext);
    }

    private CursorPageResponse searchProductsByCursor(ProductSearchRequest req) {
        Pageable pageable = PageRequest.of(0, req.size() + 1);

        List<Product> products =
                productRepository.searchProductsByKeywordWithCursor(
                        req.keyword().trim(),
                        req.cursor(),
                        pageable
                );

        boolean hasNext = products.size() > req.size();

        if (hasNext) {
            products.remove(req.size());
        }

        List<ProductSummaryResponse> resps = products.stream()
                .map(product ->
                        new ProductSummaryResponse(
                                product.getId(),
                                product.getName(),
                                product.getPrice(),
                                product.getCategory()
                        )
                ).toList();

        Long nextCursor = null;

        if (!products.isEmpty()) {
            nextCursor = products.get(products.size() - 1).getId();
        }

        return new CursorPageResponse<>(resps, nextCursor, hasNext);
    }

    private PageResponse searchProductsByOffset(ProductSearchRequest req) {
        Pageable pageable = PageRequest.of(req.page(), req.size());

        Page<ProductSummaryResponse> products;
        if (req.sort().equals("price")) {
            products =
                    productRepository.searchProductsByKeywordOrderByPrice(req.keyword(), pageable)
                            .map(product ->
                                    new ProductSummaryResponse(
                                            product.getId(),
                                            product.getName(),
                                            product.getPrice(),
                                            product.getCategory()
                                    )
                            );

        } else { // views
            products =
                    productRepository.searchProductsByKeywordOrderByViews(req.keyword(), pageable)
                            .map(product ->
                                    new ProductSummaryResponse(
                                            product.getId(),
                                            product.getName(),
                                            product.getPrice(),
                                            product.getCategory()
                                    )
                            );
        }

        return new PageResponse<>(products);
    }
}
