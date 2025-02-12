package com.korea.shop.service;

import com.korea.shop.domain.*;
import com.korea.shop.domain.item.Item;
import com.korea.shop.repository.ItemRepositoryClass;
import com.korea.shop.repository.OrderItemRepositoryClass;
import com.korea.shop.repository.OrderRepositoryClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepositoryClass orderRepository;
    private final OrderItemRepositoryClass orderItemRepository;
    private final ItemRepositoryClass itemRepository;

    // 주문서에
    // 주문서에 주문 아이템 추가
    public OrderItem addOrderItem(Long orderId, Long itemId, int price, int qty){

        Order order = orderRepository.findOne(orderId); // 주문서 불러오기
        if (order == null){
            throw new IllegalArgumentException("해당 주문 없음");
        }

        Item item = itemRepository.findOne(itemId); // 상품 불러오기
        if (item == null){
            throw new IllegalArgumentException("해당 상품 없음");
        }
        OrderItem orderItem = OrderItem.createOrderItem(order, item, price, qty);

        return orderItemRepository.save(orderItem);
    }

    // 부분 취소 = 주문 아이템 일부삭제
    public void removeOrderItem(Long orderItemId){

        // orderItemId 가지고 해당하는 orderItem객체 찾기
        OrderItem orderItem = orderItemRepository.findOne(orderItemId);

        //재고 복원
        orderItem.cancel();
        
        orderItemRepository.delete(orderItem);
    }

    // 전체 주문 취소 = 전체 주문 아이템 삭제
    public void removeAllOrderItems(Long orderId){

        Order order = orderRepository.findOne(orderId);
        if (order == null){
            throw new IllegalArgumentException("해당 주문 없음");
        }

        // 해당 주문서의 모든 아이템
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(order.getId());
        // 재고 복원
        for (OrderItem orderItem : orderItemList){
            orderItem.cancel();
        }

        
        orderItemRepository.deleteByOrderId(order);
    }
}
