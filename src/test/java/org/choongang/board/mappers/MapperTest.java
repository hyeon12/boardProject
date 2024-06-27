package org.choongang.board.mappers;

import org.apache.ibatis.session.SqlSession;
import org.choongang.board.constants.Authority;
import org.choongang.board.entities.Board;
import org.choongang.global.config.DBConn;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MapperTest {

    private BoardMapper mapper;
    private SqlSession session;
    String bId;

    @BeforeEach
    void init(){
        session = DBConn.getSession(false);
        mapper = session.getMapper(BoardMapper.class);
        bId = "test" + System.currentTimeMillis();
    }

    @Test
    void registerTest(){
        Board board = Board.builder()
                .bId(bId)
                .bName("자유게시판")
                .rowsPerPage(20)
                .active(1)
                .activeCategory(1)
                .authority(Authority.USER)
                .category("")
                .build();
        int result = mapper.register(board);
        System.out.println(result);
    }

    @Test
    void modifyTest(){
        Board board = Board.builder()
                .bId(bId)
                .bName("자유게시판")
                .rowsPerPage(20)
                .active(1)
                .activeCategory(1)
                .authority(Authority.USER)
                .category("")
                .build();
        int result = mapper.register(board);
        System.out.println(result);

        board.setBName("(수정)자유게시판");
        int result2 = mapper.modify(board);
        System.out.println(result2);

        Board board2 = mapper.get(board.getBId());
        System.out.println(board2); // 수정한 내용 조회되는지 테스트
    }

    @AfterEach
    void destroy(){
        session.rollback();
    }
}
