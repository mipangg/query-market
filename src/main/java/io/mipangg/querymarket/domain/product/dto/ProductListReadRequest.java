package io.mipangg.querymarket.domain.product.dto;

import io.mipangg.querymarket.domain.product.entity.Category;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record ProductListReadRequest(

        @Min(0)
        @Max(100)
        Integer page,

        @Min(0)
        @Max(100)
        Integer size,

        @Pattern(regexp = "latest|price|views")
        String sort,

        Category category

) {

    public ProductListReadRequest {
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 10;
        }

        if (sort == null || sort.isBlank()) {
            sort = "latest";
        }
    }

}
