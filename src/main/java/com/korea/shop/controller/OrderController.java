 package com.korea.shop.controller;


import com.korea.shop.domain.OrderItem;
import com.korea.shop.dto.OrderDTO;
import com.korea.shop.repository.OrderRepositoryClass;
import com.korea.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepositoryClass orderRepository;

    // API에서 데이터 요청들어오면 리포지토리 -> 엔티티 -> DTO변환해서 리턴해줘야함
    // 엔티티를 dto로 변환해서 내보내는 이유?
    // 1) 무한루프 문제 - Lazy loading & 양방향 연관관계일 경우
    // 2) Lazy loading 성능 문제( = sql 여러번 실행) = fetch join 해결
    // 3) api 응답시 엔티티가 영향을 받을 수 있음
    // 엔티티와 요청한 데이터가 다름 - 요청한 것에 맞춰 엔티티를 수정해야 하는 상황이 생김
    // DTO를 사용하여 엔티티와 API 응답을 분리함
    // 주문서 전체 조회
    @GetMapping("/orders")
    public List<OrderDTO> orders(){
        return orderService.findAll_ver1();
    }

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
