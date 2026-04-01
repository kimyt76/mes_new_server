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

    /** 문서번호 Get **/
    public String getDocNo (String itemGb, String type) {
        // 원재료 : JQP01-02 // 부재료 : JQP02-02 // 반제품 : JQP03-02 // 완제품 : JQP04-02
        String docNo = "";
        switch (itemGb) {
            case "1" : docNo = (type.equals("A"))? "JQP01-01" : "JQP01-02"; break;
            case "2" : docNo = (type.equals("A"))? "JQP02-01" : "JQP02-02"; break;
            case "3" : docNo = (type.equals("A"))? "JQP03-01" : "JQP03-02"; break;
            case "6" : docNo = (type.equals("A"))? "JQP04-01" : "JQP04-02"; break;
            default:   docNo = (type.equals("A"))? "JQP04-01" : "JQP04-02"; break;
        }
        return docNo;
    }

    public String getTemplateName (String itemGb, int size) {
        String template = "";
        switch (itemGb) {
            //페이지 구분 기준 : 원료, 자재, 완제품 15  반제품 16
            case "1" : template = (size <= 15)? "qt_report_m1_page1" : "qt_report_m1_page2"; break;
            case "2" : template = (size <= 15)? "qt_report_m2_page1" : "qt_report_m2_page2"; break;
            case "3" : template = (size <= 16)? "qt_report_m3_page1" : "qt_report_m3_page2"; break;
            case "6" : template = (size <= 15)? "qt_report_m6_page1" : "qt_report_m6_page2"; break;
            default:   template = (size <= 15)? "qt_report_m6_page1" : "qt_report_m6_page2"; break;
        }
        return template;
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
