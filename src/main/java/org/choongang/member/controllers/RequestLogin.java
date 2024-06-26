package org.choongang.member.controllers;

import lombok.Data;

@Data
public class RequestLogin {
    private String email;
    private String password;
    private boolean saveEmail; //이메일 기억하기
    private String redirectUrl;
}
