package io.mipangg.querymarket.domain.common;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CursorPageResponse<T> implements ProductPageResponse {

    private List<T> content;        // 현재 페이지 데이터 목록
    private Long nextCursor;     // 다음 커서
    private boolean hasNext;        // 다음 페이지 존재 여부

}
