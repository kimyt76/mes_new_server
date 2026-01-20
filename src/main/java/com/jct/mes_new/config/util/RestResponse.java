package com.jct.mes_new.config.util;

import lombok.Data;

@Data
public class RestResponse<T> {

    private int code;       // 0: 성공, 그 외: 실패
    private String message; // 메시지
    private T data;         // 실제 데이터

    public static <T> RestResponse<T> ok(T data) {
        RestResponse<T> r = new RestResponse<>();
        r.code = 0;
        r.message = "success";
        r.data = data;
        return r;
    }
    public static <T> RestResponse<T> okMessage(String message,T data) {
        RestResponse<T> r = new RestResponse<>();
        r.code = 0;
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T, obj> RestResponse<T> fail(String message,T data) {
        RestResponse<T> r = new RestResponse<>();
        r.code = -1;
        r.message = "fail";
        return r;
    }

    // getter/setter
}
