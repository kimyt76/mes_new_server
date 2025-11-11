package com.jct.mes_new.config.util;

import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.Map;

public class ExcelStyleUtil {

        // 스타일 캐시 (Workbook마다 다르게 보관)
        private static final Map<Workbook, Map<String, CellStyle>> styleCache = new HashMap<>();

        /**
         * 숫자/문자 등 정렬방향에 맞는 Border 스타일 가져오기
         *
         * @param wb Workbook
         * @param alignType LEFT, CENTER, RIGHT
         */
        public static CellStyle getBorderStyle(Workbook wb, String alignType) {
            // 캐시 맵 가져오기
            Map<String, CellStyle> cached = styleCache.computeIfAbsent(wb, k -> new HashMap<>());
            // 이미 만들어진 스타일이 있으면 반환
            if (cached.containsKey(alignType)) {
                return cached.get(alignType);
            }
            // 새 스타일 생성
            CellStyle style = wb.createCellStyle();

            // 1폰트 설정
            Font font = wb.createFont();
            font.setFontName("맑은 고딕");
            font.setFontHeightInPoints((short) 10);
            font.setBold(false);
            style.setFont(font);

            // 2테두리
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);

            // 3정렬
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            if ("LEFT".equalsIgnoreCase(alignType)) {
                style.setAlignment(HorizontalAlignment.LEFT);
            } else if ("RIGHT".equalsIgnoreCase(alignType)) {
                style.setAlignment(HorizontalAlignment.RIGHT);
            } else {
                style.setAlignment(HorizontalAlignment.CENTER);
            }
            // 4캐시에 저장
            cached.put(alignType, style);

            return style;
        }

    public static CellStyle getBoldBorderStyle(Workbook wb, String alignType) {
        // 캐시 맵 가져오기
        Map<String, CellStyle> cached = styleCache.computeIfAbsent(wb, k -> new HashMap<>());
        // 이미 만들어진 스타일이 있으면 반환
        if (cached.containsKey(alignType)) {
            return cached.get(alignType);
        }
        // 새 스타일 생성
        CellStyle style = wb.createCellStyle();

        // 1️폰트 설정
        Font font = wb.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeightInPoints((short) 10);
        font.setBold(false);
        style.setFont(font);

        // 2️테두리
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER); // 수평 가운데 정렬
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 수직 가운데 정렬

        // 3️정렬
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if ("LEFT".equalsIgnoreCase(alignType)) {
            style.setAlignment(HorizontalAlignment.LEFT);
        } else if ("RIGHT".equalsIgnoreCase(alignType)) {
            style.setAlignment(HorizontalAlignment.RIGHT);
        } else {
            style.setAlignment(HorizontalAlignment.CENTER);
        }
        // 4️캐시에 저장
        cached.put(alignType, style);

        return style;
    }

        /**
         * ✅ 헤더용 스타일 (굵은 글씨 + 가운데 정렬)
         */
        public static CellStyle getHeaderStyle(Workbook wb) {
            Map<String, CellStyle> cached = styleCache.computeIfAbsent(wb, k -> new HashMap<>());
            if (cached.containsKey("HEADER")) {
                return cached.get("HEADER");
            }

            CellStyle style = wb.createCellStyle();
            Font font = wb.createFont();
            font.setFontName("맑은 고딕");
            font.setFontHeightInPoints((short) 12);
            font.setBold(true);
            style.setFont(font);

            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            cached.put("HEADER", style);
            return style;
        }
}
