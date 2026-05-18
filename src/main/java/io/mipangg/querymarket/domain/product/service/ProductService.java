package io.mipangg.querymarket.domain.product.service;

import io.mipangg.querymarket.domain.common.PageResponse;
import io.mipangg.querymarket.domain.product.dto.ProductCreateRequest;
import io.mipangg.querymarket.domain.product.dto.ProductDetailResponse;
import io.mipangg.querymarket.domain.product.dto.ProductListReadRequest;
import io.mipangg.querymarket.domain.product.repository.ProductRepository;
import io.mipangg.querymarket.domain.product.entity.Product;
import io.mipangg.querymarket.domain.seller.entity.Seller;
import io.mipangg.querymarket.domain.seller.service.SellerService;
import io.mipangg.querymarket.global.exception.CustomLogicException;
import io.mipangg.querymarket.global.exception.ErrorCode;
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
    public void saveProduct(ProductCreateRequest req) {
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
                product.getName(),
                product.getPrice(),
                product.getSeller().getEmail(),
                product.getCategory(),
                product.getViewCount()
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductDetailResponse> getAllProducts(ProductListReadRequest req) {
        Pageable pageable;
        if (req.sort().equals("latest")) {
            pageable = PageRequest.of(req.page(), req.size(), Sort.by("createdAt").descending());
        } else if (req.sort().equals("price")) {
            pageable = PageRequest.of(req.page(), req.size(), Sort.by("price").ascending());
        } else { // views
            pageable = PageRequest.of(req.page(), req.size(), Sort.by("viewCount").descending());
        }

        Page<ProductDetailResponse> productDetailResponsePage =
                productRepository.findProductDtos(req.category(), pageable);

        return new PageResponse<>(productDetailResponsePage);
    }
}
