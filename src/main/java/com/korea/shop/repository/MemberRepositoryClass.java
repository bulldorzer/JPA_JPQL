package com.korea.shop.repository;

import com.korea.shop.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryClass {
    // 1) 엔티티 매니저
    private final EntityManager em;

    // 2) 신규 생성(저장), 업데이트(수정)
    @Transactional
    public void save(Member member){

        Long id = member.getId();

        /*Member result = em.find(Member.class, id);
        result == null ?
                em.persist(member) :
                em.merge(member);*/
        if (id != null){
            em.merge(member); // 업데이트
        }else {

            em.persist(member); // 신규 생성
        }


    }

    // 3) 1개 데이터 찾기
    public Member findOne( Long id){

        // em.find(엔티티 클래스, pk)
        return em.find(Member.class, id);
    }

    // 4) 여러개 데이터 찾기
    public List<Member> findAll(){

        // em.createQuery("jpql", 엔티티클래스);
        // jpql 기본규칙 3개지
        /*
         *   테이블 대신 엔티티명
         *   별칭 반드시 사용
         *   모든 필드 가져오기 할시 별칭으로 기재
         *   매개변수값은 ( :변수명 )형식으로 기재
         */

        return em.createQuery("select m from Member m",Member.class)
                .getResultList(); // 리스트형 변환 메서드 재공함
    }

    // 5) 이름으로 찾기
    // 기본 JpaReposity에서 제공하지 않는 경우 JPQL을 이용하여 만듦
    public List<Member> findByName(String name){

        // pk 검색 외에는 쿼리문 생성해서 실험해야함
        // Member 테이블에서 :name 이것에 해당하는 이름을 찾는 jpql 구문 작성

        return em.createQuery("select m from Member m where name = :name", Member.class)
                .setParameter("name", name)
                .getResultList(); // 리스트 형 변환 메서드 재공함
    }

    // 6) id 기준으로 삭제하기
    /*
     *   주의 : em.remove()는 영속성 컨텍스트에 존재하는 엔티티만 삭제 가능
     *   ㄴ find()를 통해 조회를 해서 영속성 컨텍스트에 가져온 후 -> 삭제 실행
     *   createQuery 메서드로 jpql 이용히여 삭제할 경우는 조회할 필요가 없음
     */
    public void deleteById(Long id){

//      em.remove(삭제할 객체);
        Member member = em.find(Member.class, id); // 조회
        if (member != null){
            em.remove(member); // 존재하면 삭제
        }

    }
}
