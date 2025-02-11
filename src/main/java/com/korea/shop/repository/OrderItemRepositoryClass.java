package com.korea.shop.repository;



import com.korea.shop.domain.Order;
import com.korea.shop.domain.OrderItem;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryClass {

    // 1 엔티티 매니저 생성
    private final EntityManager em;

    // 2) 주문 상품 저장(생성)
    public OrderItem save(OrderItem orderItem){

        em.persist(orderItem);
        return orderItem;
    }

    public OrderItem findOne(Long orderItemId){
        return em.find(OrderItem.class,orderItemId);
    }

    // 해당 주문서의 아이템 모두 조회
    public List<OrderItem> findByOrderId(Long orderId){
        return em.createQuery("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId", OrderItem.class)
                .setParameter("orderId",orderId)
                .getResultList();

    }

    // 해당 아이템 삭제 - order_id 기준
    public void delete(OrderItem orderItem){
        OrderItem oi = em.find(OrderItem.class, orderItem.getId());
        if (oi != null){
            em.remove(oi); // 조회 후 존재하면 삭제
        }
    }

    // 주문서 삭제할 경우 = 해당 상품 모두 삭제
    public void deleteByOrderId(Order order){
        Long orderId = order.getId();;
        em.createQuery("DELETE FROM OrderItem oi WHERE oi.order_id = :orderid")
                .setParameter("orderId", orderId)
                .executeUpdate(); // 적용해라
    }

}
