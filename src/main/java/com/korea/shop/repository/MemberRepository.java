package com.korea.shop.repository;

import com.korea.shop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
        1,2,3,5 - JpaRepositroy에서 기본 제공함
        4 - 따로 생성

        기본으로 제공하지 않는 구문이면
        추상 메서드에서 만들고
        @Query 애노테이션 이용하여, JPQL 작성
        --> 별도로 구현 클래스 만들 필요가 없음
     */

    @Query("select m from Member m where name= :name")
    public List<Member> findByName(String name);
}
