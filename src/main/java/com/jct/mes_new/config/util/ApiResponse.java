package com.jct.mes_new.config.util;

public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message; // Optional: 에러 메시지나 추가 정보
    private int code;       // Optional: 상태 코드

    public ApiResponse() {}

    public ApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public ApiResponse(boolean success, T data, String message, int code) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    // getter / setter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    // static helper methods
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data);
    }

    public static <T> ApiResponse<T> fail(String message, int code) {
        return new ApiResponse<>(false, null, message, code);
    }
}
