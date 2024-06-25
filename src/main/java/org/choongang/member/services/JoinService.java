package org.choongang.member.services;

import lombok.RequiredArgsConstructor;
import org.choongang.global.config.annotations.Service;
import org.choongang.member.controllers.RequestJoin;
import org.choongang.member.mapper.MemberMapper;
import org.choongang.member.validators.JoinValidator;

@Service
@RequiredArgsConstructor
public class JoinService {
    //의존성 정의
    private final JoinValidator validator;
    private final MemberMapper mapper;

    public void process(RequestJoin form){
        validator.check(form);// 회원가입 -> 회원 데이터 검증
    }

}
