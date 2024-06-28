package org.choongang.board.entities;

import lombok.Builder;
import lombok.Data;
import org.choongang.board.constants.Authority;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    //분류 목록
    public List<String> getCategories(){
        if(category != null){

            List<String> categories = Arrays.stream(category.trim().split("\\n"))
                    .map(s -> s.replace("\\r", ""))
                    .toList();

            return categories;
        }

        return Collections.EMPTY_LIST; //오류 방지 (NullPointerException)
    }
}
