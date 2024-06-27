package org.choongang.board.validators.configs;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.choongang.admin.board.controllers.RequestBoard;
import org.choongang.board.mappers.BoardMapper;
import org.choongang.global.config.annotations.Component;
import org.choongang.global.exceptions.AlertException;
import org.choongang.global.validators.RequiredValidator;
import org.choongang.global.validators.Validator;

@Component //BoardConfigService 의 의존객체이므로, Component
@RequiredArgsConstructor
public class BoardConfigValidator implements Validator<RequestBoard>, RequiredValidator {

    private final BoardMapper mapper; //게시판 중복 여부 확인하기 위해, 의존성 추가

    @Override
    public void check(RequestBoard form) {

        String bId = form.getBId();
        String bName = form.getBName();
        int status = HttpServletResponse.SC_BAD_REQUEST;

        //필수 항목 검증 - bId(게시판 아이디), bName(게시판 명)
        checkRequired(bId, new AlertException("게시판 아이디를 입력하세요.", status));
        checkRequired(bName, new AlertException("게시판 이름을 입력하세요.", status));

        //게시판 설정 "등록"인 경우 - 게시판 중복 여부 체크
        String mode = form.getMode();
        mode = mode == null || mode.isBlank() ? "register" : mode;
        //오류방지를 위해 없을때는 mode = register

        if(mode.equals("register")){
            checkTrue(mapper.exists(bId) < 1, new AlertException("이미 등록된 게시판 아이디 입니다.", status));
        }


    }
}
