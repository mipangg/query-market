package io.mipangg.querymarket.domain.product.dto;

import io.mipangg.querymarket.domain.product.entity.Category;
import java.math.BigDecimal;

public record ProductDetailResponse(
        Long id,
        String name,
        BigDecimal price,
        String sellerEmail,
        Category category,
        long viewCount
) {

}
