package org.choongang.member.mappers;

import org.choongang.member.entities.Member;

public interface MemberMapper {
    Member get(String email);
    int exists(String email); //회원 존재 유무
    int register(Member member);
}
