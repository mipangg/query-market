package io.mipangg.querymarket.domain.product.dto;

import io.mipangg.querymarket.domain.product.entity.Category;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProductCreateRequest(

        @NotBlank
        String name,

        @NotNull
        @PositiveOrZero
        BigDecimal price,

        @Email
        @NotBlank
        String sellerEmail,

        @NotNull
        Category category
) {
}
