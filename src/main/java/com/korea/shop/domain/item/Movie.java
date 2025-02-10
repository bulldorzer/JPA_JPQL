package com.korea.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("M") // 구분자컬럼 - DTYPE에 M 저장됨
@Getter
@Setter
public class Movie extends Item{
    private String director;
    private String actor;
}
