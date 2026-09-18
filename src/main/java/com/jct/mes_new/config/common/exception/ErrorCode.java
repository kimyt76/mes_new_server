package com.jct.mes_new.config.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // ===== CRUD 공통 =====
    CREATED(HttpStatus.CREATED, "success.created"),
    UPDATED(HttpStatus.OK, "success.updated"),
    DELETED(HttpStatus.OK, "success.deleted"),
    FOUND(HttpStatus.OK, "success.found"),
    QRCODE(HttpStatus.OK, "success.qrCode"),

    // ===== 오류 카테고리 =====
    NOT_FOUND(HttpStatus.NOT_FOUND, "error.not_found"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "error.unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "error.forbidden"),
    VALIDATION(HttpStatus.BAD_REQUEST, "error.validation"),
    DUPLICATE(HttpStatus.CONFLICT, "error.duplicate"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.internal"),
    INVALID_PARAMETER(HttpStatus.INTERNAL_SERVER_ERROR, "error.parameter"),

    FAIL_CREATED(HttpStatus.INTERNAL_SERVER_ERROR, "fail.created"),
    FAIL_UPDATED(HttpStatus.INTERNAL_SERVER_ERROR, "fail.updated"),
    FAIL_DELETED(HttpStatus.INTERNAL_SERVER_ERROR, "fail.deleted"),
    FAIL_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "fail.found"),
    FAIL_QRCODE(HttpStatus.INTERNAL_SERVER_ERROR, "fail.qrCode"),
    FAIL_MAIL(HttpStatus.INTERNAL_SERVER_ERROR, "fail.mail");

    private final HttpStatus httpStatus;
    private final String messageKey;

}
