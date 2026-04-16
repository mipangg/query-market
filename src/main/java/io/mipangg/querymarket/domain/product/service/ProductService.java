package io.mipangg.querymarket.domain.product.service;

import io.mipangg.querymarket.domain.product.dto.ProductCreateRequest;
import io.mipangg.querymarket.domain.product.dto.ProductDetailResponse;
import io.mipangg.querymarket.domain.product.repository.ProductRepository;
import io.mipangg.querymarket.domain.product.entity.Product;
import io.mipangg.querymarket.domain.seller.entity.Seller;
import io.mipangg.querymarket.domain.seller.service.SellerService;
import io.mipangg.querymarket.exception.CustomLogicException;
import io.mipangg.querymarket.exception.ErrorCode;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final SellerService sellerService;

    @Transactional
    public void saveProduct(ProductCreateRequest resq) {
        Seller seller = sellerService.getOrCreateSeller(resq.sellerEmail());

        productRepository.save(
                Product.builder()
                        .name(resq.name())
                        .price(resq.price())
                        .seller(seller)
                        .category(resq.category())
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

    @Transactional(readOnly = true)
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
}
