package com.korea.shop.service;

import com.korea.shop.domain.Member;
import com.korea.shop.repository.MemberRepositoryClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepositoryClass memberRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 회원가입 검증
    private void validateDuplicateMember(Member member){
        List<Member> fineMembers = memberRepository.findByName(member.getName());
        if (!fineMembers.isEmpty()){
            throw new IllegalArgumentException("이미 존재하는 회원");
        }
    }

    // 회원 전체조회
    public List<Member> findMembers(){

        return memberRepository.findAll();
    }

    // 회원 1명조회
    public Member findOne(Long memberId){

        Member result = memberRepository.findOne(memberId);

        return result;
    }
}
