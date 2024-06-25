package org.choongang.global.exceptions;

//알림 메세지 보이고, back(뒤로 가기) 하는 형태
public class AlertBackException extends AlertException{
    private String target;

    //tagget : 알림 메시지가 표시된 후 어떤 창(현재 창 또는 부모 창)으로 돌아갈지를 지정
    public AlertBackException(String message, int status, String target){
        super(message, status);
        this.target = target;
    }

    //타겟 창을 지정하지 않는 경우에 기본적으로 "self"(현재창)로 설정
    public AlertBackException(String message, int status){
        this(message, status, "self");
    }

    public String getTarget(){
        return target;
    }
}
