package com.korea.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // order가 db 예약어이기 때문에 테이블명 변경
@Getter @Setter
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 외래키 필드이름
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id") // 양방향일 경우 - 연결관계 주인 설정
    private Delivery delivery; // 배송정보
    
    public LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // [ORDER, CANCEL]

    // 주문서(Order) 생성 메서드
    public static Order createOrder(Member member, Delivery delivery) {

        Order order = new Order();
        order.setMember(member); // 주문자
        order.setDelivery(delivery); // 배송정보 설정
        order.setStatus(OrderStatus.ORDER); // 주문 상태
        order.setOrderDate(LocalDateTime.now()); // 주문시간

        return order;
    }

    private void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    // 주문취소 상태 변경
    private void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalArgumentException("이미 배송완료된 상품은 취소가 불가능 합니다.");
        }
        
        this.setStatus(OrderStatus.CANCEL); // 상태변경
    }
}
