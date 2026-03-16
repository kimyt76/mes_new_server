package com.jct.mes_new.config.util;


import lombok.experimental.UtilityClass;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class CodeUtil {

    public String convertAreaCd(String areaCd){
        return switch (areaCd) {
            case "A001" -> "1";
            case "A002" -> "2";
            case "A003" -> "1";
            default -> throw new IllegalArgumentException("잘못된 areaCd: " + areaCd);
        };
    }

    public String convertItemTypeCd(String itemTypeCd){
        return switch (itemTypeCd) {
            case "M0" -> "4";
            case "M1" -> "1";
            case "M2" -> "2";
            case "M3" -> "4";
            case "M4" -> "7";
            case "M5" -> "5";
            case "M6" -> "6";
            case "M7" -> "8";
            default -> throw new IllegalArgumentException("잘못된 itemTypeCd: " + itemTypeCd);
        };
    }

    public String createTestNo(LocalDate purDate, String areaCd, String itemTypeCd) {
        String datePrefix = purDate.format(DateTimeFormatter.ofPattern("yyMMdd"));
        String areaGb = convertAreaCd(areaCd);
        String itemGb = convertItemTypeCd(itemTypeCd);

        System.out.println("datePrefix      = [" + datePrefix + "]");
        System.out.println("areaGb       = [" + areaGb + "]");
        System.out.println("itemGb   = [" + itemGb + "]");

        return datePrefix + areaGb + itemGb;
    }
}
