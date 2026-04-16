package io.mipangg.querymarket.domain.product.entity;

import lombok.Getter;

@Getter
public enum Category {

    FASHION("패션"),
    ELECTRONICS("전자제품"),
    SPORTS("스포츠"),
    FURNITURE("가구"),
    HOUSEHOLD("생활용품"),
    FOOD("식품"),
    ETC("기타");

    private final String categoryName;

    Category(String categoryName) {
        this.categoryName = categoryName;
    }

}