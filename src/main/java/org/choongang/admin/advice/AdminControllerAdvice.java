package org.choongang.admin.advice;

import lombok.RequiredArgsConstructor;
import org.choongang.global.Interceptor;
import org.choongang.global.config.annotations.ControllerAdvice;
import org.choongang.global.exceptions.UnAuthorizedException;
import org.choongang.member.MemberUtil;

@RequiredArgsConstructor
@ControllerAdvice("org.choongang.admin")
public class AdminControllerAdvice implements Interceptor {

    private final MemberUtil memberUtil;

    @Override
    public boolean preHandle() {

        if(!memberUtil.isAdmin()){
            throw new UnAuthorizedException();//관리자 아닐땐 401 페이지 출력
        }

        return true;
    }
}
