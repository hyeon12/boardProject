package org.choongang.main.controllers;

import org.choongang.global.config.annotations.Controller;
import org.choongang.global.config.annotations.GetMapping;
import org.choongang.global.config.annotations.RequestMapping;

//BeanContainer 에서 관리하는 컨트롤러 객체 (@Controller) 명시
@Controller
@RequestMapping("/") //두 가지 형태 모두 인식되도록 {"", "/"}
public class MainController {

    //index() 반환값 => 보여질 메인 페이지 (index.jsp)
    @GetMapping
    public String index(){
        return "main/index";
    }
}
