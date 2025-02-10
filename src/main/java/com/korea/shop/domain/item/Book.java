package com.korea.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("B") // 구분자컬럼 - DTYPE에 B 저장됨
@Getter
@Setter
public class Book extends Item{
    private String author;
    private String isbn;
}
