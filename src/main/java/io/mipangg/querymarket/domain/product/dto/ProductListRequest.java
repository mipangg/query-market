package io.mipangg.querymarket.domain.product.dto;

import io.mipangg.querymarket.domain.product.entity.Category;
import io.mipangg.querymarket.global.validation.PaginationValidator;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record ProductListRequest(

        @Nullable
        Long cursor,

        @Min(0)
        Integer page,

        @Min(0)
        @Max(100)
        Integer size,

        @Pattern(regexp = "latest|price|views")
        String sort,

        Category category

) {

    public ProductListRequest {
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 20;
        }

        if (sort == null || sort.isBlank()) {
            if (cursor != null) {
                sort = "latest";
            } else {
                sort = "views";
            }
        }

        PaginationValidator.validatePaginationStrategy(sort, cursor);
    }

}
