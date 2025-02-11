package com.korea.shop.controller;


import com.korea.shop.domain.OrderItem;
import com.korea.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문서에 아이템 추가
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItem> addOrderItem(
            @PathVariable Long orderId,
            @RequestParam Long itemId,
            @RequestParam int price,
            @RequestParam int qty
    ){

        OrderItem orderItem = orderService.addOrderItem(orderId,itemId,price,qty);

        return ResponseEntity.ok(orderItem);
    }

    @DeleteMapping("/{orderId}/items")
    public ResponseEntity<Void> removeAllOrderItems(@PathVariable Long orderId){
        orderService.removeAllOrderItems(orderId);
        return ResponseEntity.noContent().build();
    }
}
