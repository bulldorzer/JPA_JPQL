package com.korea.shop.service;

import com.korea.shop.domain.Member;
import com.korea.shop.service.MemberService;
import com.korea.shop.repository.MemberRepositoryClass;
import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;




@ExtendWith(SpringExtension.class)// JUnit5 버전으로 테스트
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepositoryClass memberRepository;
    @Autowired EntityManager em;

    @Test
    public void 회원가입() throws Exception{
        // 1. given - 객체생성(값설정)
        Member member = new Member();
        member.setName("Lee");
        member.setPw("1111");
        member.setEmail("user1000@aaa.com");

        // 2. when - ~을 때
        Long saveId = memberService.join(member);

        // 3. then - 결과적으로..
        Member savedMember = memberRepository.findOne(saveId);
        System.out.println("저장된 id : "+saveId);
        System.out.println("저장된 객체 : "+savedMember);

        assertEquals(member,memberRepository.findOne(saveId));
        System.out.println("---출력--");
    }

    @Test
    public void 중복확인() throws Exception{
        // 1. given - 객체생성(값설정)
        Member mem1 = new Member();
        mem1.setName("Lee");

        Member mem2 = new Member();
        mem2.setName("Lee"); // 예외를 발생할 예정  IllegalArgumentException

        // 2. when - 동작
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->{
            memberService.join(mem1);
            memberService.join(mem2); // 예외를 발생할 예정  IllegalArgumentException
        });

        // 3. then - 결과적으로..
        // 예외 메세지가 기대하는 메세지가 맞는지 확인
        assertEquals("이미 존재하는 회원", exception.getMessage());

    }

}
