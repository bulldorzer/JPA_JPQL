package com.korea.shop.service;

import com.korea.shop.domain.*;
import com.korea.shop.domain.item.Item;
import com.korea.shop.repository.ItemRepositoryClass;
import com.korea.shop.repository.MemberRepositoryClass;
import com.korea.shop.repository.OrderItemRepositoryClass;
import com.korea.shop.repository.OrderRepositoryClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepositoryClass orderRepository;
    private final OrderItemRepositoryClass orderItemRepository;
    private final ItemRepositoryClass itemRepository;
    private final MemberRepositoryClass memberRepository;


    // 주문서에 주문서 생성만 순수하게 - 아이템 추가 X
    public Long order(Long memberId){
        Order order = new Order();
        Member member = memberRepository.findOne(memberId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        // 주문서 생성
        order.setMember(member);
        order.setDelivery(delivery);
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now()); // 현재시간 설정


        // 주문 저장
        orderRepository.save(order);



        return order.getId();

    }

    // 주문서에
    // 주문서에 주문 아이템 추가
    public OrderItem addOrderItem(Long orderId, Long itemId,  int qty){

        Order order = orderRepository.findOne(orderId); // 주문서 불러오기
        if (order == null){
            throw new IllegalArgumentException("해당 주문 없음");
        }

        Item item = itemRepository.findOne(itemId); // 상품 불러오기
        if (item == null){
            throw new IllegalArgumentException("해당 상품 없음");
        }
        
        // 쿠폰 또는 할인율 적용하는 로직
        /*double discountRatio = 0.1;
        int price = discountRatio!= 0 ? (int)Math.floor(item.getPrice() * discountRatio) : item.getPrice() ;*/

        int price = item.getPrice();
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

        // 주문 상태 - 취소로 변경
        order.cancel();

        // 재고 복원
        for (OrderItem orderItem : orderItemList){
            orderItem.cancel();
        }

        // 주문서 삭제는 경우에 따라 다르다 보통 주문 취소도 리스트로 남아 있음
        // 쿠팡) 주문서 삭제 누르면 삭제 됨
//        orderItemRepository.deleteByOrderId(order);
    }
}
