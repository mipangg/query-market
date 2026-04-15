package io.mipangg.querymarket.domain.product;

import io.mipangg.querymarket.domain.seller.Seller;
import io.mipangg.querymarket.domain.seller.SellerService;
import io.mipangg.querymarket.exception.CustomLogicException;
import io.mipangg.querymarket.exception.ErrorCode;
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
}
