package io.mipangg.querymarket.domain.product.service;

import io.mipangg.querymarket.domain.common.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PageResponse<ProductSummaryResponse> getProducts(ProductListRequest req) {
        Pageable pageable = createPageable(req.page(), req.size(), req.sort());

        Page<ProductSummaryResponse> productSummaryResponsePage =
                productRepository.findProducts(req.category(), pageable);

        return new PageResponse<>(productSummaryResponsePage);
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getPopularProducts() {

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
    public PageResponse<ProductSummaryResponse> searchProducts(ProductSearchRequest req) {

        Pageable pageable = createPageable(req.page(), req.size(), req.sort());

        Page<ProductSummaryResponse> productSummaryResponsePage =
                productRepository.searchProductsByKeyword(req.keyword().trim(), pageable);

        return new PageResponse<>(productSummaryResponsePage);
    }

    private Pageable createPageable(int page, int size, String sort) {
        if (sort.equals("latest")) {
            return PageRequest.of(page, size, Sort.by("createdAt").descending());
        } else if (sort.equals("price")) {
            return PageRequest.of(page, size, Sort.by("price").ascending());
        } else { // views
            return PageRequest.of(page, size, Sort.by("viewCount").descending());
        }
    }
}
