package org.choongang.member.validators;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.choongang.global.config.annotations.Component;
import org.choongang.global.validators.EmailValidator;
import org.choongang.global.validators.RequiredValidator;
import org.choongang.global.validators.Validator;
import org.choongang.member.controllers.RequestJoin;
import org.choongang.member.mapper.MemberMapper;

@Component
@RequiredArgsConstructor
public class JoinValidator implements Validator<RequestJoin>, RequiredValidator, EmailValidator {
    //검증을 위한 데이터가 필요함 -> RequestJoin, RequiredValidator, EmailValidator

    private final MemberMapper mapper;

    @Override
    public void check(RequestJoin form) {

    }
}
