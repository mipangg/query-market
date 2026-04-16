package io.mipangg.querymarket.domain.product.dto;

import io.mipangg.querymarket.domain.product.entity.Category;
import java.math.BigDecimal;

public record ProductCreateRequest(
        String name,
        BigDecimal price,
        String sellerEmail,
        Category category
) {
}
