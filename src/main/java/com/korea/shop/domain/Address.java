package com.korea.shop.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

// 엔티티 안에 들어가는 값 타입으로 사용될 클래스임
// 값타입으로 포함시킬 수 있다.
@Embeddable
public class Address {
    private String city;
    private String street;
    private int zipcode;
}
