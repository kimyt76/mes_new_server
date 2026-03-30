package com.jct.mes_new.config.common.exception;

import com.jct.mes_new.config.common.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // 1️⃣ 비즈니스 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusiness(BusinessException e, Locale locale) {

        // ✔ ErrorCode 있는 경우
        if (e.getErrorCode() != null) {
            ErrorCode code = e.getErrorCode();

            String msg = messageSource.getMessage(
                    code.getMessageKey(),
                    null,
                    code.name(),
                    locale
            );

            return ResponseEntity
                    .status(code.getHttpStatus())
                    .body(ApiResponse.fail(msg, code.name(), e.getDetails()));
        }

        // ✔ ErrorCode 없는 경우 (문자열 메시지)
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(e.getMessage(), "BUSINESS_ERROR", e.getDetails()));
    }

    // 2️⃣ @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValid(MethodArgumentNotValidException e, Locale locale) {

        Map<String, Object> details = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fe -> details.put(fe.getField(), fe.getDefaultMessage()));

        ErrorCode code = ErrorCode.VALIDATION;

        String msg = messageSource.getMessage(
                code.getMessageKey(),
                null,
                "Validation error",
                locale
        );

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.fail(msg, code.name(), details));
    }

    // 3️⃣ 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleEtc(Exception e, Locale locale) {
        log.error("Unhandled exception", e);

        ErrorCode code = ErrorCode.INTERNAL_ERROR;

        String msg = messageSource.getMessage(
                code.getMessageKey(),
                null,
                "Internal error",
                locale
        );

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.fail(msg, code.name(), null));
    }
}