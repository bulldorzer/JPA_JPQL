package com.korea.shop.service;

import com.korea.shop.domain.*;
import com.korea.shop.domain.item.Item;
import com.korea.shop.repository.ItemRepositoryClass;
import com.korea.shop.repository.MemberRepositoryClass;
import com.korea.shop.repository.OrderRepositoryClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepositoryClass orderRepository;
    private final MemberRepositoryClass memberRepository;
    private final ItemRepositoryClass itemRepository;

    // 주문
    public Long order(Long memberId, Long itemId, int qty){
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), qty);

        Order order = Order.createOrder(member, delivery);

        orderRepository.save(order);

        return order.getId();

    }

}
