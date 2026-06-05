package io.mipangg.querymarket.global.validation;

import io.mipangg.querymarket.global.exception.CustomLogicException;
import io.mipangg.querymarket.global.exception.ErrorCode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaginationValidator {

    public void validatePaginationStrategy(String sort, Long cursor) {
        if (cursor != null && !sort.equals("latest")) {
            throw new CustomLogicException(ErrorCode.BAD_REQUEST);
        }
    }

}
