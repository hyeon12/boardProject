package org.choongang.board.entities;

import lombok.Builder;
import lombok.Data;
import org.choongang.board.constants.Authority;

@Data
@Builder
public class Board {
    private String bId;
    private String bName;
    private int rowsPerPage;
    private int active;
    private int activeCategory;
    private String category;
    private Authority authority; //ENUM 클래스 상수 사용하기 위함
}
