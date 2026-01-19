package com.jct.mes_new.config.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RequiredArgsConstructor
public class DateStringToYmdDeserializer extends JsonDeserializer<String> {
    private static final DateTimeFormatter OUT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    };

    private static final DateTimeFormatter[] DATETIME_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
    };

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String raw = p.getValueAsString();
        if (raw == null) return null;

        String s = raw.trim();
        if (s.isEmpty()) return null;

        // 1) 먼저 날짜만 파싱 시도
        for (DateTimeFormatter f : DATE_FORMATS) {
            try {
                LocalDate d = LocalDate.parse(s, f);
                return d.format(OUT);
            } catch (DateTimeParseException ignored) {}
        }

        // 2) 날짜+시간 포맷 파싱 시도 -> 날짜만 반환
        for (DateTimeFormatter f : DATETIME_FORMATS) {
            try {
                LocalDateTime dt = LocalDateTime.parse(s, f);
                return dt.toLocalDate().format(OUT);
            } catch (DateTimeParseException ignored) {}
        }

        // 3) ISO-8601(예: 2026-01-19T10:20:30, 2026-01-19T10:20:30+09:00)
        try {
            // Offset 있는 경우
            OffsetDateTime odt = OffsetDateTime.parse(s);
            return odt.toLocalDate().format(OUT);
        } catch (DateTimeParseException ignored) {}

        try {
            // Offset 없는 경우
            LocalDateTime ldt = LocalDateTime.parse(s);
            return ldt.toLocalDate().format(OUT);
        } catch (DateTimeParseException ignored) {}

        // 4) 그래도 안되면 Jackson 표준 에러로 처리
        throw ctxt.weirdStringException(s, String.class,
                "지원하지 않는 날짜 형식입니다. 예: 2026-01-19 / 2026/01/19 / 20260119 / 2026-01-19T10:20:30");
    }
}
