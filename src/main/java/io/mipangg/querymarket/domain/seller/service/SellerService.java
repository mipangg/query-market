package io.mipangg.querymarket.domain.seller.service;

import io.mipangg.querymarket.domain.seller.entity.Seller;
import io.mipangg.querymarket.domain.seller.repository.SellerRepository;
import io.mipangg.querymarket.global.exception.CustomLogicException;
import io.mipangg.querymarket.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
                                .orElseThrow(() -> new CustomLogicException(
                                        ErrorCode.CONFLICT,
                                        "이미 존재하는 이메일입니다."
                                ));
                    }
                });
    }

}
