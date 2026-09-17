package com.jct.mes_new.config.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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

    private static final int ROW_ACCESS_WINDOW_SIZE = 100;

    // 스타일 캐시 (Workbook마다 다르게 보관)
    private static final Map<Workbook, Map<String, CellStyle>> styleCache = new HashMap<>();


    /**
     * 숫자/문자 등 정렬방향에 맞는 Border 스타일 가져오기
     *
     * @param wb Workbook
     * @param alignType LEFT, CENTER, RIGHT
     */
    public static CellStyle getBorderStyle(Workbook wb, String alignType) {

        Map<String, CellStyle> cached =
                styleCache.computeIfAbsent(wb, k -> new HashMap<>());

        String cacheKey =
                "BORDER_" + alignType.toUpperCase();

        if (cached.containsKey(cacheKey)) {
            return cached.get(cacheKey);
        }

        CellStyle style = wb.createCellStyle();

        // 폰트
        Font font = wb.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeightInPoints((short) 10);
        font.setBold(false);
        style.setFont(font);

        // 테두리
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // 정렬
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        if ("LEFT".equalsIgnoreCase(alignType)) {
            style.setAlignment(HorizontalAlignment.LEFT);

        } else if ("RIGHT".equalsIgnoreCase(alignType)) {
            style.setAlignment(HorizontalAlignment.RIGHT);

        } else {
            style.setAlignment(HorizontalAlignment.CENTER);
        }

        cached.put(cacheKey, style);

        return style;
    }


    /**
     * 굵은 Border 스타일
     */
    public static CellStyle getBoldBorderStyle(Workbook wb, String alignType) {

        Map<String, CellStyle> cached =
                styleCache.computeIfAbsent(wb, k -> new HashMap<>());

        String cacheKey =
                "BOLD_BORDER_" + alignType.toUpperCase();

        if (cached.containsKey(cacheKey)) {
            return cached.get(cacheKey);
        }

        CellStyle style = wb.createCellStyle();

        // 폰트
        Font font = wb.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeightInPoints((short) 10);
        font.setBold(false);
        style.setFont(font);

        // 굵은 테두리
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);

        style.setWrapText(true);

        // 정렬
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        if ("LEFT".equalsIgnoreCase(alignType)) {
            style.setAlignment(HorizontalAlignment.LEFT);

        } else if ("RIGHT".equalsIgnoreCase(alignType)) {
            style.setAlignment(HorizontalAlignment.RIGHT);

        } else {
            style.setAlignment(HorizontalAlignment.CENTER);
        }

        cached.put(cacheKey, style);

        return style;
    }


    /**
     * 헤더용 스타일
     * 굵은 글씨 + 가운데 정렬 + 회색 배경
     */
    public static CellStyle getHeaderStyle(Workbook wb) {

        Map<String, CellStyle> cached =
                styleCache.computeIfAbsent(wb, k -> new HashMap<>());

        String cacheKey = "HEADER";

        if (cached.containsKey(cacheKey)) {
            return cached.get(cacheKey);
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

        style.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex()
        );

        style.setFillPattern(
                FillPatternType.SOLID_FOREGROUND
        );

        cached.put(cacheKey, style);

        return style;
    }


    /**
     * 보고서 최상단 제목 스타일
     */
    public static CellStyle getReportTitleStyle(Workbook wb) {

        Map<String, CellStyle> cached =
                styleCache.computeIfAbsent(wb, k -> new HashMap<>());

        String cacheKey = "REPORT_TITLE";

        if (cached.containsKey(cacheKey)) {
            return cached.get(cacheKey);
        }

        CellStyle style = wb.createCellStyle();

        Font font = wb.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);

        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        cached.put(cacheKey, style);

        return style;
    }


    /**
     * 보고서 섹션 제목 스타일
     */
    public static CellStyle getReportSectionStyle(
            Workbook wb,
            IndexedColors color
    ) {

        Map<String, CellStyle> cached =
                styleCache.computeIfAbsent(wb, k -> new HashMap<>());

        String cacheKey =
                "REPORT_SECTION_" + color.name();

        if (cached.containsKey(cacheKey)) {
            return cached.get(cacheKey);
        }

        CellStyle style = wb.createCellStyle();

        Font font = wb.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);

        style.setFont(font);

        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        style.setFillForegroundColor(
                color.getIndex()
        );

        style.setFillPattern(
                FillPatternType.SOLID_FOREGROUND
        );

        cached.put(cacheKey, style);

        return style;
    }


    /**
     * 보고서 숫자 스타일
     */
    public static CellStyle getReportNumberStyle(Workbook wb) {

        Map<String, CellStyle> cached =
                styleCache.computeIfAbsent(wb, k -> new HashMap<>());

        String cacheKey = "REPORT_NUMBER";

        if (cached.containsKey(cacheKey)) {
            return cached.get(cacheKey);
        }

        CellStyle style = wb.createCellStyle();

        Font font = wb.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeightInPoints((short) 10);
        font.setBold(false);

        style.setFont(font);

        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        DataFormat dataFormat =
                wb.createDataFormat();

        style.setDataFormat(
                dataFormat.getFormat("#,##0.###")
        );

        cached.put(cacheKey, style);

        return style;
    }


    /**
     * 보고서 합계 스타일
     */
    public static CellStyle getReportSummaryStyle(Workbook wb) {

        Map<String, CellStyle> cached =
                styleCache.computeIfAbsent(wb, k -> new HashMap<>());

        String cacheKey = "REPORT_SUMMARY";

        if (cached.containsKey(cacheKey)) {
            return cached.get(cacheKey);
        }

        CellStyle style = wb.createCellStyle();

        Font font = wb.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);

        style.setFont(font);

        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        style.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex()
        );

        style.setFillPattern(
                FillPatternType.SOLID_FOREGROUND
        );

        DataFormat dataFormat =
                wb.createDataFormat();

        style.setDataFormat(
                dataFormat.getFormat("#,##0.###")
        );

        cached.put(cacheKey, style);

        return style;
    }


    /**
     * 일반 Cell 값 입력
     */
    public static void setCellValue(
            Row row,
            int cellNum,
            Object value,
            CellStyle style
    ) {

        Cell cell =
                getCell(row, cellNum);

        if (value == null) {
            cell.setCellValue("");

        } else if (value instanceof Number) {
            cell.setCellValue(
                    ((Number) value).doubleValue()
            );

        } else {
            cell.setCellValue(
                    String.valueOf(value)
            );
        }

        if (style != null) {
            cell.setCellStyle(style);
        }
    }


    /**
     * 숫자 Cell 입력
     */
    public static void setNumberCell(
            Row row,
            int cellNum,
            Number value,
            CellStyle style
    ) {

        Cell cell =
                getCell(row, cellNum);

        cell.setCellValue(
                value == null
                        ? 0D
                        : value.doubleValue()
        );

        if (style != null) {
            cell.setCellStyle(style);
        }
    }


    /**
     * 셀 병합 + 병합영역 전체 스타일 적용
     */
    public static void mergeAndStyle(
            Sheet sheet,
            int firstRow,
            int lastRow,
            int firstCol,
            int lastCol,
            CellStyle style
    ) {

        sheet.addMergedRegion(
                new CellRangeAddress(
                        firstRow,
                        lastRow,
                        firstCol,
                        lastCol
                )
        );

        for (int rowNum = firstRow; rowNum <= lastRow; rowNum++) {

            Row row =
                    getRow(sheet, rowNum);

            for (int col = firstCol; col <= lastCol; col++) {

                Cell cell =
                        getCell(row, col);

                if (style != null) {
                    cell.setCellStyle(style);
                }
            }
        }
    }


    /**
     * Header Row 생성
     */
    public static void createHeader(
            Sheet sheet,
            int rowNum,
            String[] headers,
            CellStyle style
    ) {

        Row row =
                getRow(sheet, rowNum);

        row.setHeightInPoints(22);

        for (int i = 0; i < headers.length; i++) {

            Cell cell =
                    getCell(row, i);

            cell.setCellValue(headers[i]);

            if (style != null) {
                cell.setCellStyle(style);
            }
        }
    }


    /**
     * Workbook 생성
     */
    public static Workbook createWorkbook() {

        return new XSSFWorkbook();
    }


    public static Workbook createWorkbook(
            InputStream fileStream
    ) throws Exception {

        return new XSSFWorkbook(fileStream);
    }


    public static Workbook createHssfWorkbook(
            InputStream fileStream
    ) throws Exception {

        return new HSSFWorkbook(fileStream);
    }


    public static Workbook createSxssfWorkbook(
            XSSFWorkbook xssfWorkbook
    ) {

        return new SXSSFWorkbook(
                xssfWorkbook,
                ROW_ACCESS_WINDOW_SIZE
        );
    }


    public static Workbook getWorkbook(String filename) {

        try (FileInputStream stream =
                     new FileInputStream(filename)) {

            return new XSSFWorkbook(stream);

        } catch (Throwable e) {

            e.printStackTrace();

            return null;
        }
    }


    public static Workbook getHssfWorkbook(String filename) {

        try (FileInputStream stream =
                     new FileInputStream(filename)) {

            return new HSSFWorkbook(stream);

        } catch (Throwable e) {

            e.printStackTrace();

            return null;
        }
    }


    /**
     * Row 가져오기
     * 없으면 생성
     */
    public static Row getRow(
            Sheet sheet,
            int rownum
    ) {

        Row row =
                sheet.getRow(rownum);

        if (row == null) {
            row = sheet.createRow(rownum);
        }

        return row;
    }


    /**
     * 특정 Row의 스타일 목록
     */
    public static List<CellStyle> getRowCellStyle(
            Sheet sheet,
            int rownum,
            int columnCount
    ) {

        List<CellStyle> cellStyleList =
                new ArrayList<>();

        for (int i = 0; i < columnCount; i++) {

            cellStyleList.add(
                    getCell(
                            sheet,
                            rownum,
                            i
                    ).getCellStyle()
            );
        }

        return cellStyleList;
    }


    /**
     * Cell 가져오기
     * 없으면 생성
     */
    public static Cell getCell(
            Row row,
            int cellnum
    ) {

        Cell cell =
                row.getCell(cellnum);

        if (cell == null) {
            cell = row.createCell(cellnum);
        }

        return cell;
    }


    /**
     * A1, B3 등의 CellReference로 Cell 조회
     */
    public static Cell getCellRef(
            Sheet sheet,
            String ref
    ) {

        CellReference cellReference =
                new CellReference(ref);

        Row row =
                getRow(
                        sheet,
                        cellReference.getRow()
                );

        return getCell(
                row,
                cellReference.getCol()
        );
    }


    public static Cell getCell(
            Sheet sheet,
            int rownum,
            int cellnum
    ) {

        Row row =
                getRow(sheet, rownum);

        return getCell(row, cellnum);
    }


    /**
     * 스타일 적용 Cell 가져오기
     */
    public static Cell getStyleCell(
            Sheet sheet,
            int rownum,
            int cellnum,
            CellStyle style
    ) {

        Row row =
                getRow(sheet, rownum);

        Cell cell =
                getCell(row, cellnum);

        cell.setCellStyle(style);

        return cell;
    }


    /**
     * XLSX 다운로드 Header
     */
    public static HttpHeaders getHeader(
            String fileName,
            int contentsLength
    ) {

        HttpHeaders header =
                new HttpHeaders();

        header.setContentType(
                new MediaType(
                        "application",
                        "vnd.ms-excel"
                )
        );

        header.set(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + fileName + ".xlsx"
        );

        header.setContentLength(contentsLength);

        return header;
    }


    /**
     * XLS 다운로드 Header
     */
    public static HttpHeaders getHeader(
            String fileName,
            int contentsLength,
            String ext
    ) {
        HttpHeaders header =
                new HttpHeaders();

        header.setContentType(
                new MediaType(
                        "application",
                        "vnd.ms-excel"
                )
        );

        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".xls");
        header.setContentLength(contentsLength);

        return header;
    }

    /**
     * Workbook -> byte[]
     */
    public static byte[] toByteArray(Workbook workbook) {

        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);

            return baos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("엑셀 파일 생성 중 오류가 발생했습니다.", ex);
        }
    }


    /**
     * 실제 파일로 저장
     */
    public static void writeExcel(
            Workbook workbook,
            String filepath
    ) {

        try (FileOutputStream stream =
                     new FileOutputStream(filepath)) {

            workbook.write(stream);

        } catch (Throwable e) {

            e.printStackTrace();
        }
    }


    /**
     * 해당 Workbook의 스타일 캐시 제거
     *
     * Workbook 사용 종료 후 필요하면 호출
     */
    public static void clearStyleCache(
            Workbook workbook
    ) {

        styleCache.remove(workbook);
    }

    public static void applyFont(
            Workbook wb,
            CellStyle style,
            String fontName,
            short fontSize
    ) {

        Font oldFont =
                wb.getFontAt(
                        style.getFontIndex()
                );

        Font font =
                wb.createFont();

        font.setFontName(fontName);
        font.setFontHeightInPoints(fontSize);
        font.setBold(oldFont.getBold());
        font.setItalic(oldFont.getItalic());

        style.setFont(font);
    }

}