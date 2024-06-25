package org.choongang.global.exceptions;

import jakarta.servlet.http.HttpServletResponse;

//검증 실패시 나올 내용 (CommonException의 하위 클래스)
//응답코드 : 400 으로 제한
public class BadRequestException extends CommonException{
    public BadRequestException(String message){
        super(message, HttpServletResponse.SC_BAD_REQUEST);
    }

    public BadRequestException(){
        this("잘못된 요청입니다.");
    }
}
