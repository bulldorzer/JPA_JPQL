package com.korea.shop.dto;


import com.korea.shop.domain.*;

import java.time.LocalDateTime;

public class OrderDTO {

    private Long orderId;
    private String name; // Member 대신 주문자 이름
    private LocalDateTime orderDate; // 주문시간
    private OrderStatus status; // [ORDER, CANCEL]
    private Address address; // 주문서에 - 배송지 주소

     public OrderDTO(Order order){
        this.orderId = order.getId();
        this.name = order.getMember().getName();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.address = order.getDelivery().getAddress();
     }

}
