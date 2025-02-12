package com.korea.shop.service;


import com.korea.shop.domain.Member;
import com.korea.shop.domain.Order;
import com.korea.shop.domain.OrderItem;
import com.korea.shop.domain.OrderStatus;
import com.korea.shop.domain.item.Book;
import com.korea.shop.exception.NotEoughStockException;
import com.korea.shop.repository.OrderItemRepositoryClass;
import com.korea.shop.repository.OrderRepositoryClass;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(SpringExtension.class) //JUnit 5
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepositoryClass orderRepository;
    @Autowired OrderItemRepositoryClass orderItemRepository;

    // 주문서 생성 = 상품주문
    @Test
    public void 상품주문() throws Exception{
        // given
        // memberId, deliveryId orderDate, orderStatus
        Member member = createMember();
        Book item = createBook("JAVA Spring",10000,10);
        int orderQty=3;// 주문수량

        // when
        // 상품 주문
        Long orderId = orderService.order(member.getId());

        // 주문 상품 추가
        OrderItem orderItem = orderService.addOrderItem(orderId,item.getId(),item.getPrice(),orderQty);

        // 주문 상품 리스트 조회
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("상품 주문서 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("재고조회", 7,item.getStockQuantity());

        
        
        // 기대하는 숫자와 비교하기 1-assertEquals 사용
        assertEquals("상품조회",1,orderItemList.size());

        // 총 주문 금액계산
//        int totalPrice = orderItemList.stream().mapToInt(oi->oi.getTotalPrice()).sum();
        int totalPrice = orderItemList.stream().mapToInt(OrderItem::getTotalPrice).sum();
        /*for (OrderItem oi : orderItemList){
            totalPrice += oi.getTotalPrice();
        }*/
        assertEquals("주문 금액",item.getPrice()*orderQty,totalPrice);

//        System.out.println(" 재고조회결과: "+(7==item.getStockQuantity()));
//        System.out.println("상품조회 결과: "+(1==orderItemList.size()));

    }

    // 재고 수량의 초과, 예외 발생해야함
    @Test
    public void 수량의초과예외() throws Exception{
        // given
        Member member = createMember();
        Book item = createBook("JAVA Spring",10000,10);
        int orderQty=15;// 주문수량

        Long orderId = orderService.order(member.getId());
        // when
        NotEoughStockException exception = assertThrows(NotEoughStockException.class,()->{
            OrderItem orderItem = orderService.addOrderItem(orderId,item.getId(),item.getPrice(),orderQty);
        });

        // then
        assertEquals("재고부족", exception.getMessage());
    }
    // 부분취소
    @Test
    public void 부분취소() throws Exception{
        // given

        // when

        // then
    }
    // 전체취소
    @Test
    public void 전체취소() throws Exception{
        // given
        Member member = createMember();
        Book item = createBook("JAVA Spring",10000,10);
        int orderQty=2;// 주문수량
        Long orderId = orderService.order(member.getId());
        OrderItem orderItem = orderService.addOrderItem(orderId,item.getId(),item.getPrice(),orderQty);
        // when
        // 주문서 취소
        System.out.println("order_id : "+orderId);
        orderService.removeAllOrderItems(orderId);


        // then
        Order getOrder = orderRepository.findOne(orderId);
        // 재고수량 증가
        assertEquals(OrderStatus.CANCEL,getOrder.getStatus(),"주문 상태 확인");
        assertEquals(OrderStatus.CANCEL,getOrder.getStatus(),"주문 상태 확인");


        // 상태 체크 - OrderStatus가 cancel인 상태 체크

    }

    private Member createMember(){
        Member member = new Member();
        member.setName("회원1");
        member.setEmail("user100@aaa.com");
        member.setPw("1111");
        em.persist(member); // 영속성 컨텍스트에 저장

        return member;
    }

    private Book createBook(String name, int price, int stockqty){
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockqty);
        em.persist(book); // 영속성 컨텍스트에 저장
        return book;
    }
}
