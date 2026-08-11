package com.jct.mes_new.config.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ComUtil {
    /**
     * null이면 빈 문자열 반환
     */
    public static String nvl(Object value) {
        return value == null
                ? ""
                : String.valueOf(value);
    }

    /**
     * null이면 defaultValue 반환
     */
    public static String nvl(Object value, String defaultValue) {
        return value == null
                ? defaultValue
                : String.valueOf(value);
    }
}
