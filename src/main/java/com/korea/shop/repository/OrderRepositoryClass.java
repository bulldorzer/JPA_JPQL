package com.korea.shop.repository;



import com.korea.shop.domain.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryClass {

    // 1 엔티티 매니저 생성
    /*
    *   [엔티티 매니저 메서드 종류]
    *   - persist(객체) : 저장
    *   - find(엔티티.class,pk) : 1개 찾기
    *   - merge(객체) : 존재0 - 수정 존재X - 생성
    *   - remove(객체) : 해당 데이터 삭제
    *
    *   [쿼리생성]
    *   - createQuery("jpql구문", 엔티티.class) - 쿼리발생
    *   - setParameter("파라미터명", 값) - 파미터 설정 매개변수
    *   - getResultList : 리스트 형태로 데이터 추출
    *   - executeUpdate() : DML 실행시에 쿼리 실행 ( insert, update, delete )
    *
    *
    *   flush() : 영속성 컨텍스트 변경내용 db에 즉시반영
    *   clear() : 영속성 컨텍스트 비우기
    *   detach() : 영속성 상태 -> 준영속 상태로 만든다.
    *
    *   영속 상태 (persistent state)
    *   - persist(), find() 사용하면 영속 상태가 됨, 엔티티 반경하면 DB에 자동반영
    *
    *   준영속 상태 - JPA 관리 안하는 상태 = DB랑 관계 없음
    *   - detach(엔티티) 호출 : JPA가 해당 엔티티 관리X 상태 -> 변경해도 DB반영
    *   - 엔티티 자동변경을 안하고 싶을때 ( = 불필요한 변경 방지 하고 싶을 때)
    *   - 메모리 관리 (준영속 상태가 되면 가비지 컬렉터의 수거 대상이 됨, 불필요할때 사라지게 됨)
    *   - merge(엔티티) 사용하면 다시 영속 상태로 변경 가능.
    */
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

    // 3) orderId 기준으로  주문서 검색
    public Order findOne(Long orderId){

        // em.find(엔티티 클래스, pk)
        return em.find(Order.class, orderId);
    }

    // 주문서 검색 방식 1 JPQL
    public List<Order> findAllByString(OrderSearch orderSearch){

        StringBuilder jpql = new StringBuilder(" select o from Order o join o.member m "); // 가변형 텍스트
        List<String> condition = new ArrayList<>(); // 조건이 str이 저장될 리스트

        // status 검색조건
        if (orderSearch.getOrderStatus() != null){
            condition.add("o.status = :status");
            
        }

        // 회원 이름 검색
        // StringUtils.hasText() - 문자열이 null 또는 공백이 아닌 실제 텍스트인지 확인
        // null, "", " " 와 같은 값들을 확인 방지하고 싶을때 사용하면 좋음
        if (StringUtils.hasText(orderSearch.getMemberName())){
            condition.add("m.name like :name");

        }
        /*
            condition 경우의 수
            condition = ["o.status = :status"]
            condition = [ "m.name = :name" ]
            condition =  [ "o.status = :status", "m.name = :name" ]
         */

        if (!condition.isEmpty()){ // 조건이 존재하면
            jpql.append(" where ").append(String.join(" and ",condition));
        }
        
        // 동적 쿼리 실행
        /*
            select o from order o join o.member m where o.status = :status limit 1000;
            select o from order o join o.member m where m.name like :name limit 1000;
            select o from order o join o.member m where o.status = :status and m.name like :name limit 1000;
        */
        TypedQuery<Order> query = em.createQuery(jpql.toString(), Order.class)
                .setMaxResults(1000); // 최대  1000개까지만 데이터 반환하도록 제한

        // 동적 파라미터 설정
        if (orderSearch.getOrderStatus() != null){
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())){
            query = query.setParameter("name", "%"+orderSearch.getMemberName()+"%");
        }
        return query.getResultList();
    }

    /*
        Criteria API란
        Criteria API는 JPA(Java Persistence API)에서 동적 쿼리를 생성할 수 있도록 지원하는 API임
        SQL을 직접 작성하지 않고 객체 지향방식으로 동적으로 쿼리를 생성할 수 있음.
    */
    // 주문서 검색 방식 1 Criteria API 방식
    public List<Order> findAllByCriteria(OrderSearch orderSearch){

        // Criteria API를 이용하여 동적 쿼리를 생성하는 도구
        // where, select, join 조건 만들수 있음
        // 결과물이 Order 클래스 형식적인 쿼리를 만듬 = SELECT * FROM orders와 비슷한 역할
        // 쿼리에서 Order 테이블을 기준으로 설정하겟다
        // ROOT<T>란 = CriteriaQuery에서 기준이 되는 테이블을 지정
        // 엔티티 Order, Member 이너조인
        // 조인할 테이블 지정
        // 결과 : SELECT * FROM orders o INNER JOIN member m ON o.memeberid = m.id
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Order> cq = cb.createQuery(Order.class);

        Root<Order> o = cq.from(Order.class);

        Join<Object,Object> m = o.join("member",JoinType.INNER);


        // 검색 조건을 지정할 리스트
        List<Predicate> criteria = new ArrayList<>();



        // 주문 상태 검색
        // cb.equal(테이블.get(필드명), 값);
        // where문 조건설정 where status = :status
        if (orderSearch.getOrderStatus() != null){

            Predicate status = cb.equal(o.get("status"),orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        // cb.like(테이블.get(필드명),"%"+ 값 + "%");
        // where name like "%김%"
        // Criteria API에서 cb.like와 같은 메서드에서 타입 불일치 오류 발생 가능성 줄임
        // 조건을 where절에 적용
        if (StringUtils.hasText(orderSearch.getMemberName())){
            Predicate name = cb.like(m.<String>get("name"),"%"+orderSearch.getMemberName()+"%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);

        return query.getResultList();
    }
}
