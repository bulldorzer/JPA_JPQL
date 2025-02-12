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
        Member member = createMember();

        Book item1 = createBook("JAVA Spring",10000,10);
        Book item2 = createBook("React",11000,10);

        int orderQty=2;// 주문수량
        Long orderId = orderService.order(member.getId());

        OrderItem orderItem1 = orderService.addOrderItem(orderId,item1.getId(),item1.getPrice(),orderQty);
        OrderItem orderItem2 = orderService.addOrderItem(orderId,item2.getId(),item2.getPrice(),orderQty);


        // when
        // 주문서 부분 취소
        orderService.removeOrderItem(orderItem1.getId());

        // then
        Order getOrder = orderRepository.findOne(orderId); // 주문서 다시 검색
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId); // 아이템 리스트
        int totalPrice = orderItemList.stream().mapToInt(OrderItem::getTotalPrice).sum();

        // 주문한 아이템 갯수
        assertEquals(1, orderItemList.size());
        // 삭제된 아이템의 재고가 복구 되었는가?
        assertEquals(10, item1.getStockQuantity()); // 2개주문
        // 두번째 아이템 재고는 그대로
        assertEquals(8, item2.getStockQuantity()); // 2개주문

        System.out.println("현재 주문서 아이템 갯수 : "+ orderItemList.size());
        System.out.println("전체금액 : "+ totalPrice);
        System.out.println("아이템1 재고 : "+item1.getStockQuantity());
        System.out.println("아이템2 재고 : "+item2.getStockQuantity());
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
        orderService.removeAllOrderItems(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);
        System.out.println("order_id : "+orderId);
        // 재고수량 증가
        assertEquals(10,item.getStockQuantity(),"아이템 갯수 확인");


        // 상태 체크 - OrderStatus가 cancel인 상태 체크
        assertEquals(OrderStatus.CANCEL,getOrder.getStatus(),"주문 상태 확인");

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
