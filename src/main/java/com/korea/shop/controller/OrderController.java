package com.korea.shop.controller;


import com.korea.shop.domain.OrderItem;
import com.korea.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문서에 아이템 추가
    // /api/orders/{주문서번호}/items
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItem> addOrderItem(
            @PathVariable Long orderId,
            @RequestParam Long itemId,
            @RequestParam int qty
    ){

        OrderItem orderItem = orderService.addOrderItem(orderId,itemId,qty);

        return ResponseEntity.ok(orderItem);
    }

    @DeleteMapping("/{items}/{orderItemId}")
    public ResponseEntity<Map<String, Object>> removeOrderItem(@PathVariable Long orderItemId){
        Map<String, Object> response = new HashMap<>();

        try{
            orderService.removeOrderItem(orderItemId);

            response.put("message", "주문 아이템 삭제 성공");
            response.put("orderItemId", orderItemId);
            response.put("status", "error");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "서버 오류가 발생했습니다.");
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{orderId}/items")
    public ResponseEntity<Void> removeAllOrderItems(@PathVariable Long orderId){
        orderService.removeAllOrderItems(orderId);
        return ResponseEntity.noContent().build();
    }
}
