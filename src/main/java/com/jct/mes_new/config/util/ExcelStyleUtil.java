package com.jct.mes_new.config.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelStyleUtil {

        private static int ROW_ACCESS_WINDOW_SIZE = 100;
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


    public static Workbook createWorkbook() {

        return new XSSFWorkbook();
    }

    public static Workbook createWorkbook(InputStream fileStream) throws Exception {
        return new XSSFWorkbook(fileStream);
    }

    public static Workbook createHssfWorkbook(InputStream fileStream) throws Exception {
        return new HSSFWorkbook(fileStream);
    }

    public static Workbook createSxssfWorkbook(XSSFWorkbook xssfWorkbook) {

        return new SXSSFWorkbook(xssfWorkbook, ROW_ACCESS_WINDOW_SIZE);
    }

    public static Workbook getWorkbook(String filename) {
        try (FileInputStream stream = new FileInputStream(filename)) {
            return new XSSFWorkbook(stream);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Workbook getHssfWorkbook(String filename) {
        try (FileInputStream stream = new FileInputStream(filename)) {
            return new HSSFWorkbook(stream);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Row getRow(Sheet sheet, int rownum) {
        Row row = sheet.getRow(rownum);
        if (row == null) {
            row = sheet.createRow(rownum);
        }
        return row;
    }

    public static List<CellStyle> getRowCellStyle(Sheet sheet, int rownum, int columnCount) {
        List<CellStyle> cellStyleList = new ArrayList<>();
        for (int i = 0; i < columnCount; i++ ) {
            cellStyleList.add(getCell(sheet, rownum, i).getCellStyle());
        }
        return cellStyleList;
    }

    public static Cell getCell(Row row, int cellnum) {
        Cell cell = row.getCell(cellnum);
        if (cell == null) {
            cell = row.createCell(cellnum);
        }
        return cell;
    }

    public static Cell getCellRef (Sheet sheet, String ref) {
        CellReference cellReference = new CellReference(ref);
        Row row = sheet.getRow(cellReference.getRow());
        Cell cell = row.getCell(cellReference.getCol());
        return cell;
    }

    public static Cell getCell(Sheet sheet, int rownum, int cellnum) {
        Row row = getRow(sheet, rownum);
        return getCell(row, cellnum);
    }

    public static Cell getStyleCell(Sheet sheet, int rownum, int cellnum, CellStyle style) {
        Row row = getRow(sheet, rownum);
        Cell cell = getCell(row, cellnum);
        cell.setCellStyle(style);
        return cell;
    }

    public static HttpHeaders getHeader(String fileName, int contentsLength) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "vnd.ms-excel"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".xlsx");
        header.setContentLength(contentsLength);
        return header;
    }

    public static HttpHeaders getHeader(String fileName, int contentsLength, String ext) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "vnd.ms-excel"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".xls");
        header.setContentLength(contentsLength);
        return header;
    }

    public static byte[] toByteArray(Workbook workbook) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            workbook.write(baos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return baos.toByteArray();
    }


    public static  void writeExcel(Workbook workbook, String filepath) {
        try (FileOutputStream stream = new FileOutputStream(filepath)) {
            workbook.write(stream);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
