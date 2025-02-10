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
    private int orderPrice;
    private int count;

    @ManyToOne
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;
}
