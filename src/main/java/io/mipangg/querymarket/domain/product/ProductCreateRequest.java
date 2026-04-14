package io.mipangg.querymarket.domain.product;

import java.math.BigDecimal;

public record ProductCreateRequest(
        String name,
        BigDecimal price,
        String sellerEmail,
        String category
) {
}
