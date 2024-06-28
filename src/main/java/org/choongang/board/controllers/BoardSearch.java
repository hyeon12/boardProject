package org.choongang.board.controllers;

import lombok.Data;

//검색
@Data
public class BoardSearch {
    private int page = 1;
    private int limit = 20;
    private int offset;
    private int endRows;
    private String bId; //게시판 아이디
    private String sopt = "ALL";
    private String skey;
}
