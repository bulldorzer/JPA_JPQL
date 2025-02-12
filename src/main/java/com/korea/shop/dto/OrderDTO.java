package com.korea.shop.dto;


import com.korea.shop.domain.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;
    private String name; // Member 대신 주문자 이름
    private LocalDateTime orderDate; // 주문시간
    private OrderStatus status; // [ORDER, CANCEL]
    private Address address; // 주문서에 - 배송지 주소

    // 엔티티 전체조회하고 필요한 데이터만 추출 DTO 변환
    // 장점 : 재사용성이 좋음
    // 단점 : 불필요한 데이터 조회가 될 수 있음
     public OrderDTO(Order order){
        this.orderId = order.getId();
        this.name = order.getMember().getName();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.address = order.getDelivery().getAddress();
     }

     // 조회와 동시에 DTO 변환이 가능함
     // 장점 : 필요한 데이터만 DTO에 맞게 조회해옴 - 엔티티 전체조회 X -> 성능최적화
     // 단점 : 재사용성이 맞음, 리포지토리에 API 스펙이 들어가 있음
     /*public OrderDTO(Long orderId,
                     String name, 
                     LocalDateTime orderDate, 
                     OrderStatus status, 
                     Address address){
         this.orderId = orderId;
         this.name = name;
         this.orderDate = orderDate;
         this.status = status;
         this.address = address;


     }*/

}
