package com.korea.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders") // order가 db 예약어이기 때문에 테이블명 변경
@Getter @Setter
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id") // 외래키 필드이름
    private Member member;

    @OneToOne
    @JoinColumn(name = "delivery_id") // 양방향일 경우 - 연결관계 주인 설정
    private Delivery delivery; // 배송정보

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // [ORDER, CANCEL]

}
