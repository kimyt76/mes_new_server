package com.jct.mes_new.config.common.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    // 1️⃣ ErrorCode 기반
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.details = null;
    }

    // 2️⃣ ErrorCode + 상세정보
    public BusinessException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.details = details;
    }

    // 3️⃣ 🔥 메시지 직접 전달 (가장 많이 사용)
    public BusinessException(String message) {
        super(message);
        this.errorCode = null;
        this.details = null;
    }

    // 4️⃣ 메시지 + 상세정보
    public BusinessException(String message, Map<String, Object> details) {
        super(message);
        this.errorCode = null;
        this.details = details;
    }
}
