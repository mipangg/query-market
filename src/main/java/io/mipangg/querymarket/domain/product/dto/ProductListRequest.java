package io.mipangg.querymarket.domain.product.dto;

import io.mipangg.querymarket.domain.product.entity.Category;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record ProductListRequest(

        @Nullable
        Long cursor,

        @Min(0)
        @Max(100)
        @Nullable
        Integer page,

        @Min(0)
        @Max(100)
        Integer size,

        @Pattern(regexp = "latest|price|views")
        String sort,

        Category category

) {

    public ProductListRequest {
        if (size == null) {
            size = 20;
        }

        if (sort == null || sort.isBlank()) {
            sort = "latest";
        }
    }

}
