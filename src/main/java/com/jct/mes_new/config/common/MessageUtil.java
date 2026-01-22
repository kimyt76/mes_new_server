package com.jct.mes_new.config.common;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageUtil {
    private final MessageSource messageSource;

    public String get(String key, String defaultMessage) {
        return messageSource.getMessage(
                key,
                null,
                defaultMessage,
                LocaleContextHolder.getLocale()
        );
    }

    public String get(String key) {
        try {
            return messageSource.getMessage(
                    key,
                    null,
                    LocaleContextHolder.getLocale()
            );
        } catch (NoSuchMessageException e) {
            // 키 누락을 빨리 발견하고 싶으면 그냥 key 그대로 반환 or 예외 던져도 됨
            return key; // 또는 throw e;
        }
    }
}
