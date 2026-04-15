package io.mipangg.querymarket.domain.seller;

import static io.mipangg.querymarket.TestUtils.genSellers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerServiceTests {

    @InjectMocks
    private SellerService sellerService;

    @Mock
    private SellerRepository sellerRepository;

    @Test
    @DisplayName("저장된 판매자 정보가 없으면 새로 생성 후 반환한다")
    void getOrCreateSellerSuccessCreateTest() {

        String email = "seller1@example.com";
        Seller seller = genSellers().getFirst();

        when(sellerRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        Seller result = sellerService.getOrCreateSeller(email);

        assertThat(result.getEmail()).isEqualTo(email);
        verify(sellerRepository).findByEmail(email);
        verify(sellerRepository).save(any(Seller.class));

    }

    @Test
    @DisplayName("저장된 판매자 정보가 있으면 반환한다")
    void getOrCreateSellerSuccessGetTest() {


        String email = "seller1@example.com";
        Seller seller = genSellers().getFirst();

        when(sellerRepository.findByEmail(email)).thenReturn(Optional.of(seller));

        Seller result = sellerService.getOrCreateSeller(email);

        assertThat(result.getEmail()).isEqualTo(email);
        verify(sellerRepository).findByEmail(email);
        verify(sellerRepository, never()).save(any(Seller.class));

    }

}