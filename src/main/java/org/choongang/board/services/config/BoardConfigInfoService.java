package org.choongang.board.services.config;

import lombok.RequiredArgsConstructor;
import org.choongang.admin.board.controllers.RequestBoard;
import org.choongang.board.entities.Board;
import org.choongang.board.exceptions.BoardConfigNotFoundException;
import org.choongang.board.mappers.BoardMapper;
import org.choongang.global.config.annotations.Service;

import java.util.List;
import java.util.Optional;

//강사님 스타일 -> Info (조회 기능일때)
@Service
@RequiredArgsConstructor
public class BoardConfigInfoService {

    private final BoardMapper mapper;

    //게시판 없을때 예외 처리
    public Optional<Board> get(String bId){
        Board board = mapper.get(bId);

        return Optional.ofNullable(board);//데이터 없을때는 Optional 형태로 가져옴
    }

    public RequestBoard getForm(String bId){
        Board board = get(bId).orElseThrow(BoardConfigNotFoundException::new);

        RequestBoard form = new RequestBoard();
        form.setMode("update");
        form.setBId(board.getBId());
        form.setBName(board.getBName());
        form.setRowsPerPage(board.getRowsPerPage());
        form.setActive(board.getActive() == 1);
        form.setActiveCategory(board.getActiveCategory() == 1);
        form.setCategory(board.getCategory());
        form.setAuthority(board.getAuthority().name());

        return form;
    }

    public List<Board> getList(){
        return mapper.getList();
    }
}
