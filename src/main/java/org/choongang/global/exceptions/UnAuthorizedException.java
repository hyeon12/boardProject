package org.choongang.global.exceptions;

import jakarta.servlet.http.HttpServletResponse;

//관리자 페이지 - 401 코드(접근 권한 제한)
public class UnAuthorizedException extends CommonException{
    public UnAuthorizedException(){
        super("접근 권한이 없습니다.", HttpServletResponse.SC_UNAUTHORIZED);
    }
}
