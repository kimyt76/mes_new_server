
package com.jct.mes_new.config.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;

@UtilityClass
public class AmountUtil {

    private static final String[] NUMBERS = {
            "", "일", "이", "삼", "사",
            "오", "육", "칠", "팔", "구"
    };

    private static final String[] SMALL_UNITS = {
            "", "십", "백", "천"
    };

    private static final String[] BIG_UNITS = {
            "", "만", "억", "조", "경"
    };

    /**
     * 숫자 금액을 한글 금액으로 변환
     *
     * 1,452,000    -> 일백사십오만이천
     * 1,452,000.50 -> 일백사십오만이천
     * 100,000,000  -> 일억
     *
     * 소수점 이하 버림
     * 0, null, 빈 문자열은 "" 반환
     */
    public String amtToKor(String amt) {

        // 1. NULL 및 빈값 처리
        if (amt == null || amt.trim().isEmpty()) {
            return "";
        }

        // 2. 콤마 및 공백 제거
        String value = amt.replace(",", "").trim();

        BigInteger amount;

        try {
            // 3. 소수점 이하 버림
            amount = new BigDecimal(value).toBigInteger();

        } catch (NumberFormatException e) {
            return "";
        }

        // 4. 금액이 0이면 빈 문자열
        if (amount.signum() == 0) {
            return "";
        }

        // 5. 음수 처리
        boolean negative = amount.signum() < 0;

        String number = amount.abs().toString();

        StringBuilder result = new StringBuilder();

        int groupIndex = 0;

        // 6. 오른쪽부터 4자리씩 분리
        while (!number.isEmpty()) {

            int start = Math.max(0, number.length() - 4);

            String group = number.substring(start);

            number = number.substring(0, start);

            // 7. 4자리 숫자를 한글로 변환
            String korGroup = convertGroup(group);

            if (!korGroup.isEmpty()) {

                if (groupIndex >= BIG_UNITS.length) {
                    throw new IllegalArgumentException(
                            "지원하지 않는 금액 범위입니다."
                    );
                }

                // 만, 억, 조, 경 단위 추가
                result.insert(
                        0,
                        korGroup + BIG_UNITS[groupIndex]
                );
            }

            groupIndex++;
        }

        // 8. 최종 한글 금액 반환
        return (negative ? "마이너스 " : "")
                + result.toString();
    }

    /**
     * 4자리 숫자를 한글로 변환
     *
     * 145 -> 일백사십오
     * 2000 -> 이천
     * 1000 -> 일천
     * 100  -> 일백
     * 10   -> 일십
     */
    private String convertGroup(String group) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < group.length(); i++) {

            int digit = group.charAt(i) - '0';

            // 0은 출력하지 않음
            if (digit == 0) {
                continue;
            }

            int unitIndex = group.length() - i - 1;

            result.append(NUMBERS[digit]);
            result.append(SMALL_UNITS[unitIndex]);
        }

        return result.toString();
    }
}
