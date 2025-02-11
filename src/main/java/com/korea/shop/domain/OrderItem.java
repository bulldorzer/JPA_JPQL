package com.korea.shop.domain;

import com.korea.shop.domain.item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_item_id") // db 관례적으로 작명을 snake style 사용
    private Long id;
    private int orderPrice; // 주문 가격
    private int qty;        // 주문 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    // 어느 주문서(큰틀) 에 무슨 상품을 무슨가격에 얼마나 등록할것이냐
    public static OrderItem createOrderItem(Order order, Item item, int orderPrice, int qty){
        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setQty(qty);

        item.removeStack(qty);
        return orderItem;
    }


    // 주문 취소 - 재고 다시 더하기
    public void cancel(){
        getItem().addStack(qty);
    }

    // 주문상품 - 상품담 단가 * 수량
    public int getTotalPrice(){
        return getOrderPrice() * getQty();
    }
}
