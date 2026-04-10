package com.jct.mes_new.config.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private String code;
    private T data;

    public ApiResponse(boolean success, String message, String code, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    // 성공 응답
    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(true, message, "SUCCESS", null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", "SUCCESS", data);
    }

    // 실패 응답
    public static <T> ApiResponse<T> fail(String message, String code, T data) {
        return new ApiResponse<>(false, message, code, data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, "SUCCESS", data);
    }

}
