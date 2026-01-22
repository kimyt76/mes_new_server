package com.jct.mes_new.config.common.exception;

import com.jct.mes_new.config.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // 1) 비즈니스 예외 (권한/조회없음/중복/검증 등)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e, Locale locale) {
        ErrorCode code = e.getErrorCode();

        // messageKey 기반 메시지
        String msg = messageSource.getMessage(
                code.getMessageKey(),
                null,
                code.name(), // 키가 없으면 fallback
                locale
        );

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.fail(msg, code.name(), e.getDetails()));
    }

    // 2) @Valid 검증 실패(Bean Validation) - 자동으로 들어오는 케이스
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValid(MethodArgumentNotValidException e, Locale locale) {
        Map<String, Object> details = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fe -> details.put(fe.getField(), fe.getDefaultMessage()));

        ErrorCode code = ErrorCode.VALIDATION;
        String msg = messageSource.getMessage(code.getMessageKey(), null, "Validation error", locale);

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.fail(msg, code.name(), details));
    }

    // 3) 그 외 예외 (서버 오류)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleEtc(Exception e, Locale locale) {
        ErrorCode code = ErrorCode.INTERNAL_ERROR;
        String msg = messageSource.getMessage(code.getMessageKey(), null, "Internal error", locale);

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.fail(msg, code.name(), null));
    }
}