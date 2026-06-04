package io.mipangg.querymarket.domain.product.dto;

import io.mipangg.querymarket.global.validation.PaginationValidator;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProductSearchRequest(

        @Nullable
        Long cursor,

        @Min(0)
        Integer page,

        @Min(0)
        @Max(100)
        Integer size,

        @Pattern(regexp = "latest|price|views")
        String sort,

        @NotBlank
        String keyword

) {

    public ProductSearchRequest {
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 20;
        }

        if (sort == null || sort.isBlank()) {
            if (cursor == null) {
                sort = "views";
            } else {
                sort = "latest";
            }
        }

        PaginationValidator.validatePaginationStrategy(sort, cursor);
    }

}
