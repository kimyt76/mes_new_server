package com.jct.mes_new.config.common;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;


@UtilityClass
public class CommonUtil {

    private static final SecureRandom random = new SecureRandom();

    public static String createUUId() {
        // 6자리 시간 기반: yyMMdd
        String datePart = new SimpleDateFormat("yyMMdd").format(new Date());
        // 6자리 난수
        int randomPart = random.nextInt(1_000_000); // 0~999999
        String randomPartStr = String.format("%06d", randomPart);
        return datePart + randomPartStr; // 총 12자리
    }
}
