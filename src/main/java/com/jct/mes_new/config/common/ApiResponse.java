package com.jct.mes_new.config.common;

public record ApiResponse<T>(
        boolean success,
        String message,
        String code,
        T data,
        Object details
) {
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, "OK", data, null);
    }

    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>(true, message, "OK", null, null);
    }

    public static ApiResponse<Void> fail(String message, String code, Object details) {
        return new ApiResponse<>(false, message, code, null, details);
    }
}
