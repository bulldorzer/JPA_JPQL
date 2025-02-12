package com.korea.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id") // 컬럼명 변경
    private Long id;

    private String name;
    private String email;
    private String pw;

    @Embedded // 값 타입 포함
    private Address address;

//    @OneToMany(mappedBy = "member") // 연결관계 - 거울 설정 (양방향)
//    private List<Order> orders = new ArrayList<>();

}
