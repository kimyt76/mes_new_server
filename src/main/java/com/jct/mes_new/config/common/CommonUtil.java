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

    /**
     * 18자리 트랜잭션 ID 생성
     * 형식: 13자리 타임스탬프 + 5자리 난수 (숫자만)
     */
    public static String generateUUID() {
        long timestamp = System.currentTimeMillis(); // 13자리
        int rand = random.nextInt(100_000); // 0~99999
        return timestamp + String.format("%05d", rand); // 18자리
    }

    /**
     * 접두사 포함 트랜잭션 ID 생성 (예: 서버ID)
     * 형식: 접두사 + 13자리 타임스탬프 + 3자리 난수 = 총 prefix + 16자리
     */
    public static String generatePrefixUUID(String prefix) {
        long timestamp = System.currentTimeMillis();
        int rand = random.nextInt(1000); // 0~999
        return prefix + timestamp + String.format("%03d", rand);
    }
}
