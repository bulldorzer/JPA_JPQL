package com.korea.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("A") // 구분자컬럼에 저장되는 값 - DTYPE에 A 저장됨
@Getter
@Setter
public class Album extends Item {
    private String artist;
    private String etc;
}
