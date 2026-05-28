package io.mipangg.querymarket.domain.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProductSearchRequest(
        @Min(0)
        @Max(10000)
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
            sort = "latest";
        }
    }

}
