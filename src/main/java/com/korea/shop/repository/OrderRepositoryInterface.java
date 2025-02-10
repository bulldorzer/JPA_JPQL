package com.korea.shop.repository;



import com.korea.shop.domain.Member;
import com.korea.shop.domain.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryInterface {

    // 1 엔티티 매니저 생성
    private final EntityManager em;

    // 2) 신규 생성(저장), 업데이트(수정)
    public void save(Order order){

        Long id = order.getId();

        if (id != null){
            em.merge(order); // 업데이트
        }else {

            em.persist(order); // 신규 생성
        }
    }

    // 3) 상품 id 기준으로 검색
    public Order findOne(Long id){

        // em.find(엔티티 클래스, pk)
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch){

        String jpql = " select o from Order o join o.member m ";
        boolean isFirstCondition = true; // 첫번째인지 확인

        if (orderSearch.getOrderStatus() != null){
            if (isFirstCondition){
                jpql += " where "; // 조건문 추가
                isFirstCondition = false;
            } else {
                jpql += " and ";
            }
            jpql += " o.status = :status ";
        }

        // 회원 이름 검색
        // StringUtils.hasText() - 문자열이 null 또는 공백이 아닌 실제 텍스트인지 확인
        // null, "", " " 와 같은 값들을 확인 방지하고 싶을때 사용하면 좋음
        if (StringUtils.hasText(orderSearch.getMemberName())){
            if (isFirstCondition){
                jpql += " where "; // 조건문 추가
                isFirstCondition = false;
            } else {
                jpql += " and ";
            }
            jpql += " m.name like :name ";

        }
        
        // 동적 쿼리 실행
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); // 최대  1000개까지만 데이터 반환하도록 제한

        // 동적 파라미터 설정
        if (orderSearch.getOrderStatus() != null){
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())){
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    /*
        Criteria API란
        Criteria API는 JPA(Java Persistence API)에서 동적 쿼리를 생성할 수 있도록 지원하는 API임
        SQL을 직접 작성하지 않고 객체 지향방식으로 동적으로 쿼리를 생성할 수 있음.
    */
    public List<Order> findAllByCriteria(OrderSearch orderSearch){

        // Criteria API를 이용하여 동적 쿼리를 생성하는 도구
        // where, select, join 조건 만들수 있음
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // 결과물이 Order 클래스 형식적인 쿼리를 만듬 = SELECT * FROM orders와 비슷한 역할
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);

        // 쿼리에서 Order 테이블을 기준으로 설정하겟다
        // ROOT<T>란 = CriteriaQuery에서 기준이 되는 테이블을 지정
        Root<Order> o = cq.from(Order.class);

        // Order, Member 이너조인
        // 조인할 테이블 지정
        Join<Object,Object> m = o.join("member", JoinType.INNER);

        // 결과 : SELECT * FROM orders o INNER JOIN member m ON o.memeberid = m.id

        return List.of();
    }
}
