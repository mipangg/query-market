package io.mipangg.querymarket.domain.seller;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    @Transactional
    public Seller getOrCreateSeller(String email) {
        return sellerRepository.findByEmail(email)
                .orElseGet(() -> {
                    try {
                        return sellerRepository.save(
                                Seller.builder()
                                        .email(email)
                                        .build()
                        );

                    } catch (DataIntegrityViolationException e) {
                        return sellerRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("판매자 생성 실패"));
                    }
                });
    }

}
