package com.korea.shop.repository;


import com.korea.shop.domain.item.Item;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryClass {

    // 1 엔티티 매니저 생성
    private final EntityManager em;

    // 2) 신규 생성(저장), 업데이트(수정)
    public void save(Item item){

        Long id = item.getId();

        if (id != null){
            em.merge(item); // 업데이트
        }else {

            em.persist(item); // 신규 생성
        }
    }

    // 3) 상품 id 기준으로 검색
    public Item findOne(Long id){

        // em.find(엔티티 클래스, pk)
        return em.find(Item.class, id);
    }

    // 4) 전체 상품 검색 - findAll()
    public List<Item> findAll(){

        // em.createQuery("jpql", 엔티티클래스);
        // jpql 기본규칙 3개지
        /*
         *   테이블 대신 엔티티명
         *   별칭 반드시 사용
         *   모든 필드 가져오기 할시 별칭으로 기재
         *   매개변수값은 ( :변수명 )형식으로 기재
         */

        String sql = "select i from Item i";

        return em.createQuery(sql, Item.class)
                .getResultList(); // 리스트형 변환 메서드 제공
    }
}
