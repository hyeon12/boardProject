package org.choongang.member.entities;

import lombok.Builder;
import lombok.Data;
import org.choongang.member.constants.UserType;

import java.time.LocalDateTime;

@Data
@Builder
public class Member {
    private long userNo;
    private String email;
    private String password;
    private String userName;
    private UserType userType = UserType.USER;
    //값을 입력하는 이유? => enum 상수 인식하도록
    private LocalDateTime regDt;
    private LocalDateTime modDt;
}
