package com.jct.mes_new.biz.base.excel;


import com.jct.mes_new.biz.base.mapper.M2DailyReportMapper;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class M2DailyReportExcel {

    private final M2DailyReportMapper m2DailyReportMapper;

    public byte[] download(DailyReportVo vo) {

        Workbook workbook = ExcelStyleUtil.createWorkbook();

        try {
            Sheet sheet = workbook.createSheet("부자재 일일 입고 및 사용내역");
            sheet.setDisplayGridlines(false);

            // =========================================================
            // 컬럼 폭
            // =========================================================
            sheet.setColumnWidth(0, 14 * 256);   // 일자
            sheet.setColumnWidth(1, 8 * 256);    // NO / 제품명 1
            sheet.setColumnWidth(2, 16 * 256);   // 품목코드 / 제품명 2
            sheet.setColumnWidth(3, 26 * 256);   // 거래처명 / 생산수량
            sheet.setColumnWidth(4, 70 * 256);   // 품명 / BOM
            sheet.setColumnWidth(5, 14 * 256);   // 수량 / 소요량
            sheet.setColumnWidth(6, 14 * 256);   // 단가
            sheet.setColumnWidth(7, 17 * 256);   // 공급가액 / 합계
            sheet.setColumnWidth(8, 17 * 256);   // 창고 / 총합계
            sheet.setColumnWidth(9, 16 * 256);   // 개당단가
            sheet.setColumnWidth(10, 24 * 256);  // 비고

            // =========================================================
            // 스타일
            // =========================================================
            CellStyle titleStyle = ExcelStyleUtil.getReportTitleStyle(workbook);
            CellStyle headerStyle = ExcelStyleUtil.getHeaderStyle(workbook);
            CellStyle leftStyle = ExcelStyleUtil.getBorderStyle(workbook, "LEFT");
            CellStyle centerStyle = ExcelStyleUtil.getBorderStyle(workbook, "CENTER");
            CellStyle numberStyle = ExcelStyleUtil.getReportNumberStyle(workbook);
            CellStyle summaryStyle = ExcelStyleUtil.getReportSummaryStyle(workbook);

            // 헤더 줄바꿈 허용
            headerStyle.setWrapText(true);

            // 마지막 제품별 부자재 사용량의 제품명 전용 스타일
            // 셀 폭보다 긴 경우 아래 줄로 계속 표시
            CellStyle productNameStyle = workbook.createCellStyle();
            productNameStyle.cloneStyleFrom(centerStyle);
            productNameStyle.setWrapText(true);
            productNameStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // =========================================================
            // 굴림 13pt
            // =========================================================
            ExcelStyleUtil.applyFont(
                    workbook,
                    headerStyle,
                    "굴림",
                    (short) 13
            );

            ExcelStyleUtil.applyFont(
                    workbook,
                    leftStyle,
                    "굴림",
                    (short) 13
            );

            ExcelStyleUtil.applyFont(
                    workbook,
                    productNameStyle,
                    "굴림",
                    (short) 13
            );

            ExcelStyleUtil.applyFont(
                    workbook,
                    centerStyle,
                    "굴림",
                    (short) 13
            );

            ExcelStyleUtil.applyFont(
                    workbook,
                    numberStyle,
                    "굴림",
                    (short) 13
            );

            ExcelStyleUtil.applyFont(
                    workbook,
                    summaryStyle,
                    "굴림",
                    (short) 13
            );

            // =========================================================
            // 일반 숫자
            // 소수점 제거
            // =========================================================
            CellStyle integerStyle = workbook.createCellStyle();

            integerStyle.cloneStyleFrom(numberStyle);

            integerStyle.setDataFormat(
                    workbook
                            .createDataFormat()
                            .getFormat("#,##0")
            );

            // =========================================================
            // 합계 숫자
            // 소수점 제거
            // =========================================================
            CellStyle summaryNumberStyle = workbook.createCellStyle();

            summaryNumberStyle.cloneStyleFrom(summaryStyle);

            summaryNumberStyle.setAlignment(
                    HorizontalAlignment.RIGHT
            );

            summaryNumberStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            summaryNumberStyle.setDataFormat(
                    workbook
                            .createDataFormat()
                            .getFormat("#,##0")
            );

            // =========================================================
            // 합계 제목
            // 가운데 정렬
            // =========================================================
            CellStyle summaryCenterStyle = workbook.createCellStyle();

            summaryCenterStyle.cloneStyleFrom(summaryStyle);

            summaryCenterStyle.setAlignment(
                    HorizontalAlignment.CENTER
            );

            summaryCenterStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            // =========================================================
            // 데이터 조회
            // =========================================================
            DailyReportVo report = new DailyReportVo();

            report.setDailyId(vo.getDailyId());
            report.setItemTypeCd("M2");

            // 입고
            report.setTranTypeCd("A");
            List<DailyReportVo> inList = m2DailyReportMapper.getDailyReportList(report);

            // 반품
            report.setTranTypeCd("P");
            List<DailyReportVo> returnList = m2DailyReportMapper.getDailyReportList(report);

            // 불량
            report.setTranTypeCd("G");
            List<DailyReportVo> discardList = m2DailyReportMapper.getDailyReportList(report);

            // 제품별 부자재 사용량
            List<DailyReportVo> useList = m2DailyReportMapper.getUseList(report);

            int rowNum = 0;

            // =========================================================
            // 제목
            // =========================================================
            Row titleRow = ExcelStyleUtil.getRow(sheet, rowNum);

            titleRow.setHeightInPoints(30);

            ExcelStyleUtil.setCellValue(
                    titleRow,
                    0,
                    "부자재 일일 입고 및 사용내역",
                    titleStyle
            );

            ExcelStyleUtil.mergeAndStyle(
                    sheet,
                    rowNum,
                    rowNum,
                    0,
                    10,
                    titleStyle
            );

            rowNum += 2;

            // =========================================================
            // 1. 당일 부자재 입고 현황
            // =========================================================
            rowNum = createM2CommonSection(
                    workbook,
                    sheet,
                    rowNum,
                    "1. 당일 부자재 입고 현황",
                    "입고일자",
                    "입고창고",
                    inList,
                    IndexedColors.LIGHT_CORNFLOWER_BLUE,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle
            );

            rowNum++;

            // =========================================================
            // 2. 당일 부자재 반품 내역
            // =========================================================
            rowNum = createM2CommonSection(
                    workbook,
                    sheet,
                    rowNum,
                    "2. 당일 부자재 반품 내역",
                    "반품일자",
                    "반품창고",
                    returnList,
                    IndexedColors.LIGHT_ORANGE,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle
            );

            rowNum++;

            // =========================================================
            // 3. 당일 부자재 불량 및 폐기 내역
            // =========================================================
            rowNum = createM2DiscardSection(
                    workbook,
                    sheet,
                    rowNum,
                    discardList,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle
            );

            rowNum++;

            // =========================================================
            // 4. 제품별 부자재 사용량
            // =========================================================
            createM2UseSection(
                    workbook,
                    sheet,
                    rowNum,
                    useList,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle,
                    productNameStyle
            );

            return ExcelStyleUtil.toByteArray(workbook);

        } catch (Exception e) {
            throw new RuntimeException("부자재 일일보고서 엑셀 생성 중 오류가 발생했습니다.", e);
        } finally {
            ExcelStyleUtil.clearStyleCache(workbook);

            try {
                workbook.close();
            } catch (Exception ignored) {
            }
        }
    }


    // =========================================================
    // 1. 입고 / 2. 반품 공통
    // 비고 9 ~ 10 병합
    // =========================================================
    private int createM2CommonSection(
            Workbook workbook,
            Sheet sheet,
            int rowNum,
            String title,
            String dateHeader,
            String storageHeader,
            List<DailyReportVo> list,
            IndexedColors sectionColor,
            CellStyle headerStyle,
            CellStyle leftStyle,
            CellStyle centerStyle,
            CellStyle numberStyle,
            CellStyle summaryCenterStyle,
            CellStyle summaryNumberStyle
    ) {

        CellStyle sectionStyle =
                ExcelStyleUtil.getReportSectionStyle(
                        workbook,
                        sectionColor
                );

        ExcelStyleUtil.applyFont(
                workbook,
                sectionStyle,
                "굴림",
                (short) 13
        );

        // =========================================================
        // 섹션 제목
        // =========================================================
        Row sectionRow = ExcelStyleUtil.getRow(sheet, rowNum);

        sectionRow.setHeightInPoints(24);

        ExcelStyleUtil.setCellValue(
                sectionRow,
                0,
                title,
                sectionStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                0,
                10,
                sectionStyle
        );

        rowNum++;

        // =========================================================
        // 헤더
        // =========================================================
        Row headerRow = ExcelStyleUtil.getRow(sheet, rowNum);

        headerRow.setHeightInPoints(24);

        ExcelStyleUtil.setCellValue(
                headerRow,
                0,
                dateHeader,
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                1,
                "NO",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                2,
                "품목코드",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                3,
                "거래처명",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                4,
                "품명",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                5,
                "수량[EA]",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                6,
                "단가",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                7,
                "공급가액",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                8,
                storageHeader,
                headerStyle
        );

        // 비고 9 ~ 10
        ExcelStyleUtil.setCellValue(
                headerRow,
                9,
                "비고",
                headerStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                9,
                10,
                headerStyle
        );

        rowNum++;

        double totalQty = 0D;
        double totalAmount = 0D;

        // =========================================================
        // 데이터
        // =========================================================
        if (list != null) {

            for (DailyReportVo item : list) {

                Row row =
                        ExcelStyleUtil.getRow(
                                sheet,
                                rowNum
                        );

                row.setHeightInPoints(23);

                ExcelStyleUtil.setCellValue(
                        row,
                        0,
                        item.getDailyDate(),
                        centerStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        1,
                        item.getOrderDist(),
                        centerStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        2,
                        item.getItemCd(),
                        centerStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        3,
                        item.getCustomerName(),
                        leftStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        4,
                        item.getItemName(),
                        leftStyle
                );

                double qty =
                        toDouble(
                                item.getQty()
                        );

                double inPrice =
                        toDouble(
                                item.getInPrice()
                        );

                double amount =
                        qty * inPrice;

                ExcelStyleUtil.setNumberCell(
                        row,
                        5,
                        qty,
                        numberStyle
                );

                ExcelStyleUtil.setNumberCell(
                        row,
                        6,
                        inPrice,
                        numberStyle
                );

                ExcelStyleUtil.setNumberCell(
                        row,
                        7,
                        amount,
                        numberStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        8,
                        item.getStorageName(),
                        centerStyle
                );

                // 비고 9 ~ 10
                ExcelStyleUtil.setCellValue(
                        row,
                        9,
                        item.getEtc(),
                        leftStyle
                );

                ExcelStyleUtil.mergeAndStyle(
                        sheet,
                        rowNum,
                        rowNum,
                        9,
                        10,
                        leftStyle
                );

                totalQty += qty;
                totalAmount += amount;

                rowNum++;
            }
        }

        // =========================================================
        // 합계
        // =========================================================
        Row summaryRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        rowNum
                );

        summaryRow.setHeightInPoints(23);

        ExcelStyleUtil.setCellValue(
                summaryRow,
                0,
                "합계",
                summaryCenterStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                0,
                4,
                summaryCenterStyle
        );

        ExcelStyleUtil.setNumberCell(
                summaryRow,
                5,
                totalQty,
                summaryNumberStyle
        );

        ExcelStyleUtil.setCellValue(
                summaryRow,
                6,
                "",
                summaryNumberStyle
        );

        ExcelStyleUtil.setNumberCell(
                summaryRow,
                7,
                totalAmount,
                summaryNumberStyle
        );

        ExcelStyleUtil.setCellValue(
                summaryRow,
                8,
                "",
                summaryNumberStyle
        );

        ExcelStyleUtil.setCellValue(
                summaryRow,
                9,
                "",
                summaryCenterStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                9,
                10,
                summaryCenterStyle
        );

        return rowNum + 1;
    }


    // =========================================================
    // 3. 당일 부자재 불량 및 폐기 내역
    // 11칸 그대로 사용
    // =========================================================
    private int createM2DiscardSection(
            Workbook workbook,
            Sheet sheet,
            int rowNum,
            List<DailyReportVo> list,
            CellStyle headerStyle,
            CellStyle leftStyle,
            CellStyle centerStyle,
            CellStyle numberStyle,
            CellStyle summaryCenterStyle,
            CellStyle summaryNumberStyle
    ) {

        CellStyle sectionStyle =
                ExcelStyleUtil.getReportSectionStyle(
                        workbook,
                        IndexedColors.GREY_25_PERCENT
                );

        ExcelStyleUtil.applyFont(
                workbook,
                sectionStyle,
                "굴림",
                (short) 13
        );

        // =========================================================
        // 섹션 제목
        // =========================================================
        Row sectionRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        rowNum
                );

        sectionRow.setHeightInPoints(24);

        ExcelStyleUtil.setCellValue(
                sectionRow,
                0,
                "3. 당일 부자재 불량 및 폐기 내역",
                sectionStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                0,
                10,
                sectionStyle
        );

        rowNum++;

        // =========================================================
        // 헤더
        // =========================================================
        String[] headers = {
                "불량일자",
                "NO",
                "품목코드",
                "거래처명",
                "품명",
                "원불량\n수량[EA]",
                "작업불량\n수량[EA]",
                "단가",
                "공급가",
                "발생장소",
                "비고"
        };

        int headerRowNum = rowNum;

        ExcelStyleUtil.createHeader(
                sheet,
                rowNum++,
                headers,
                headerStyle
        );

        // 원불량수량 / 작업불량수량 헤더가 2줄로 보이도록 높이 확보
        Row headerRow = sheet.getRow(headerRowNum);
        if (headerRow != null) {
            headerRow.setHeightInPoints(36);
        }

        double totalQty = 0D;
        double totalAmount = 0D;

        // =========================================================
        // 데이터
        // =========================================================
        if (list != null) {

            for (DailyReportVo item : list) {

                Row row =
                        ExcelStyleUtil.getRow(
                                sheet,
                                rowNum++
                        );

                row.setHeightInPoints(23);

                ExcelStyleUtil.setCellValue(
                        row,
                        0,
                        item.getDailyDate(),
                        centerStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        1,
                        item.getOrderDist(),
                        centerStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        2,
                        item.getItemCd(),
                        centerStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        3,
                        item.getCustomerName(),
                        leftStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        4,
                        item.getItemName(),
                        leftStyle
                );

                double badQty =
                        toDouble(
                                item.getBadQty()
                        );

                double workBadQty =
                        toDouble(
                                item.getWorkBadQty()
                        );

                double inPrice =
                        toDouble(
                                item.getInPrice()
                        );

                double qty =
                        badQty + workBadQty;

                double amount =
                        qty * inPrice;

                ExcelStyleUtil.setNumberCell(
                        row,
                        5,
                        badQty,
                        numberStyle
                );

                ExcelStyleUtil.setNumberCell(
                        row,
                        6,
                        workBadQty,
                        numberStyle
                );

                ExcelStyleUtil.setNumberCell(
                        row,
                        7,
                        inPrice,
                        numberStyle
                );

                ExcelStyleUtil.setNumberCell(
                        row,
                        8,
                        amount,
                        numberStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        9,
                        item.getStorageName(),
                        centerStyle
                );

                ExcelStyleUtil.setCellValue(
                        row,
                        10,
                        item.getEtc(),
                        leftStyle
                );

                totalQty += qty;
                totalAmount += amount;
            }
        }

        // =========================================================
        // 합계
        // =========================================================
        Row summaryRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        rowNum
                );

        summaryRow.setHeightInPoints(23);

        ExcelStyleUtil.setCellValue(
                summaryRow,
                0,
                "합계",
                summaryCenterStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                0,
                4,
                summaryCenterStyle
        );

        // 원불량 + 작업불량
        ExcelStyleUtil.setNumberCell(
                summaryRow,
                5,
                totalQty,
                summaryNumberStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                5,
                6,
                summaryNumberStyle
        );

        ExcelStyleUtil.setCellValue(
                summaryRow,
                7,
                "",
                summaryNumberStyle
        );

        ExcelStyleUtil.setNumberCell(
                summaryRow,
                8,
                totalAmount,
                summaryNumberStyle
        );

        ExcelStyleUtil.setCellValue(
                summaryRow,
                9,
                "",
                summaryNumberStyle
        );

        ExcelStyleUtil.setCellValue(
                summaryRow,
                10,
                "",
                summaryNumberStyle
        );

        return rowNum + 1;
    }


    // =========================================================
    // 4. 제품별 부자재 사용량
    //
    // 0      사용일자
    // 1~2    제품명
    // 3      생산수량
    // 4      BOM
    // 5      소요량
    // 6      단가
    // 7      합계
    // 8      총합계
    // 9      개당단가
    // 10     비고
    // =========================================================
    private int createM2UseSection(
            Workbook workbook,
            Sheet sheet,
            int rowNum,
            List<DailyReportVo> list,
            CellStyle headerStyle,
            CellStyle leftStyle,
            CellStyle centerStyle,
            CellStyle numberStyle,
            CellStyle summaryCenterStyle,
            CellStyle summaryNumberStyle,
            CellStyle productNameStyle
    ) {

        CellStyle sectionStyle =
                ExcelStyleUtil.getReportSectionStyle(
                        workbook,
                        IndexedColors.LIGHT_GREEN
                );

        ExcelStyleUtil.applyFont(
                workbook,
                sectionStyle,
                "굴림",
                (short) 13
        );

        // =========================================================
        // 섹션 제목
        // =========================================================
        Row sectionRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        rowNum
                );

        sectionRow.setHeightInPoints(24);

        ExcelStyleUtil.setCellValue(
                sectionRow,
                0,
                "4. 제품별 부자재 사용량",
                sectionStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                0,
                10,
                sectionStyle
        );

        rowNum++;

        // =========================================================
        // Header
        // =========================================================
        Row headerRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        rowNum
                );

        headerRow.setHeightInPoints(24);

        ExcelStyleUtil.setCellValue(
                headerRow,
                0,
                "사용일자",
                headerStyle
        );

        // 제품명 1 ~ 2
        ExcelStyleUtil.setCellValue(
                headerRow,
                1,
                "제품명",
                headerStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                1,
                2,
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                3,
                "생산수량[EA]",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                4,
                "BOM",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                5,
                "소요량[EA]",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                6,
                "단가",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                7,
                "합계",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                8,
                "총합계",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                9,
                "개당단가[원]",
                headerStyle
        );

        // 비고는 마지막 1칸만
        ExcelStyleUtil.setCellValue(
                headerRow,
                10,
                "비고",
                headerStyle
        );

        rowNum++;

        // =========================================================
        // 제품별 그룹
        // =========================================================
        List<UseGroup> groupedList =
                groupUseList(list);

        double requiredTotal = 0D;
        double sumPriceTotal = 0D;
        double totalPriceTotal = 0D;

        // =========================================================
        // Data
        // =========================================================
        for (UseGroup group : groupedList) {

            if (group.rows.isEmpty()) {
                continue;
            }

            int groupStartRow = rowNum;
            int groupSize = group.rows.size();
            int groupEndRow = groupStartRow + groupSize - 1;

            DailyReportVo master =
                    group.rows.get(0);

            double prodQty =
                    toDouble(
                            master.getProdQty()
                    );

            double groupTotalPrice = 0D;

            // =========================================================
            // 제품별 BOM 총금액
            // =========================================================
            for (DailyReportVo item : group.rows) {

                double requiredQty =
                        toDouble(
                                item.getRequiredQuantity()
                        );

                double inPrice =
                        toDouble(
                                item.getInPrice()
                        );

                groupTotalPrice +=
                        requiredQty * inPrice;
            }

            double unitPrice =
                    prodQty > 0D
                            ? groupTotalPrice / prodQty
                            : 0D;

            // =========================================================
            // BOM Row
            // =========================================================
            for (int i = 0; i < group.rows.size(); i++) {

                DailyReportVo item =
                        group.rows.get(i);

                Row row =
                        ExcelStyleUtil.getRow(
                                sheet,
                                rowNum
                        );

                row.setHeightInPoints(23);

                double requiredQty =
                        toDouble(
                                item.getRequiredQuantity()
                        );

                double inPrice =
                        toDouble(
                                item.getInPrice()
                        );

                double sumPrice =
                        requiredQty * inPrice;

                // =====================================================
                // 제품 공통값
                // 첫 번째 행에만 값 입력
                // =====================================================
                if (i == 0) {

                    ExcelStyleUtil.setCellValue(
                            row,
                            0,
                            master.getDailyDate(),
                            centerStyle
                    );

                    ExcelStyleUtil.setCellValue(
                            row,
                            1,
                            getPackingItemName(master),
                            productNameStyle
                    );

                    ExcelStyleUtil.setNumberCell(
                            row,
                            3,
                            prodQty,
                            numberStyle
                    );

                    ExcelStyleUtil.setNumberCell(
                            row,
                            8,
                            groupTotalPrice,
                            numberStyle
                    );

                    ExcelStyleUtil.setNumberCell(
                            row,
                            9,
                            unitPrice,
                            numberStyle
                    );
                }

                // BOM
                ExcelStyleUtil.setCellValue(
                        row,
                        4,
                        item.getBomName(),
                        leftStyle
                );

                // 소요량
                ExcelStyleUtil.setNumberCell(
                        row,
                        5,
                        requiredQty,
                        numberStyle
                );

                // 단가
                ExcelStyleUtil.setNumberCell(
                        row,
                        6,
                        inPrice,
                        numberStyle
                );

                // 합계
                ExcelStyleUtil.setNumberCell(
                        row,
                        7,
                        sumPrice,
                        numberStyle
                );

                // 비고 1칸
                ExcelStyleUtil.setCellValue(
                        row,
                        10,
                        item.getEtc(),
                        leftStyle
                );

                requiredTotal += requiredQty;
                sumPriceTotal += sumPrice;

                rowNum++;
            }

            // =========================================================
            // 제품명이 병합된 셀 폭보다 길면 자동 줄바꿈
            // 병합 영역 전체 높이가 줄 수에 맞도록 행 높이 조정
            // =========================================================
            String productName = getPackingItemName(master);
            int charsPerLine = 24;
            int productNameLineCount = Math.max(
                    1,
                    (int) Math.ceil((double) productName.length() / charsPerLine)
            );

            float requiredProductNameHeight = productNameLineCount * 18F;
            float currentGroupHeight = groupSize * 23F;

            if (requiredProductNameHeight > currentGroupHeight) {
                float rowHeight = requiredProductNameHeight / groupSize;

                for (int r = groupStartRow; r <= groupEndRow; r++) {
                    Row groupRow = sheet.getRow(r);
                    if (groupRow != null) {
                        groupRow.setHeightInPoints(rowHeight);
                    }
                }
            }

            totalPriceTotal +=
                    groupTotalPrice;

            // =========================================================
            // 제품명
            // 반드시 1~2 가로 병합
            // =========================================================
            if (groupSize == 1) {

                ExcelStyleUtil.mergeAndStyle(
                        sheet,
                        groupStartRow,
                        groupStartRow,
                        1,
                        2,
                        productNameStyle
                );

            } else {

                // 사용일자 세로 병합
                ExcelStyleUtil.mergeAndStyle(
                        sheet,
                        groupStartRow,
                        groupEndRow,
                        0,
                        0,
                        centerStyle
                );

                // 제품명 가로 + 세로 병합
                ExcelStyleUtil.mergeAndStyle(
                        sheet,
                        groupStartRow,
                        groupEndRow,
                        1,
                        2,
                        productNameStyle
                );

                // 생산수량 세로 병합
                ExcelStyleUtil.mergeAndStyle(
                        sheet,
                        groupStartRow,
                        groupEndRow,
                        3,
                        3,
                        numberStyle
                );

                // 총합계 세로 병합
                ExcelStyleUtil.mergeAndStyle(
                        sheet,
                        groupStartRow,
                        groupEndRow,
                        8,
                        8,
                        numberStyle
                );

                // 개당단가 세로 병합
                ExcelStyleUtil.mergeAndStyle(
                        sheet,
                        groupStartRow,
                        groupEndRow,
                        9,
                        9,
                        numberStyle
                );
            }
        }

        // =========================================================
        // 합계
        // =========================================================
        Row summaryRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        rowNum
                );

        summaryRow.setHeightInPoints(23);

        ExcelStyleUtil.setCellValue(
                summaryRow,
                0,
                "합 계",
                summaryCenterStyle
        );

        // 사용일자 ~ BOM까지
        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                0,
                4,
                summaryCenterStyle
        );

        // 소요량
        ExcelStyleUtil.setNumberCell(
                summaryRow,
                5,
                requiredTotal,
                summaryNumberStyle
        );

        // 단가
        ExcelStyleUtil.setCellValue(
                summaryRow,
                6,
                "",
                summaryNumberStyle
        );

        // 합계
        ExcelStyleUtil.setNumberCell(
                summaryRow,
                7,
                sumPriceTotal,
                summaryNumberStyle
        );

        // 총합계
        ExcelStyleUtil.setNumberCell(
                summaryRow,
                8,
                totalPriceTotal,
                summaryNumberStyle
        );

        // 개당단가
        ExcelStyleUtil.setCellValue(
                summaryRow,
                9,
                "",
                summaryNumberStyle
        );

        // 비고 한 칸
        ExcelStyleUtil.setCellValue(
                summaryRow,
                10,
                "",
                summaryCenterStyle
        );

        return rowNum + 1;
    }


    // =========================================================
    // 제품별 사용량 그룹 생성
    //
    // 날짜 + 제품 + 생산수량 기준
    // =========================================================
    private List<UseGroup> groupUseList(
            List<DailyReportVo> list
    ) {

        Map<String, UseGroup> groupMap =
                new LinkedHashMap<>();

        if (list == null) {
            return new ArrayList<>();
        }

        for (int i = 0; i < list.size(); i++) {

            DailyReportVo item =
                    list.get(i);

            String productKey =
                    safeString(item.getDailyDate())
                            + "|"
                            + getPackingItemKey(item, i)
                            + "|"
                            + safeString(item.getProdQty());

            UseGroup group =
                    groupMap.computeIfAbsent(
                            productKey,
                            key -> new UseGroup()
                    );

            group.rows.add(item);
        }

        return new ArrayList<>(
                groupMap.values()
        );
    }


    // =========================================================
    // 제품 그룹 KEY
    //
    // BOM itemCd 기준으로 묶지 않는다.
    // 완제품 기준으로 그룹화한다.
    // =========================================================
    private String getPackingItemKey(
            DailyReportVo item,
            int index
    ) {

        if (item.getPackingItemCd() != null
                && !item.getPackingItemCd().isBlank()) {

            return item.getPackingItemCd();
        }

        if (item.getPackingItemName() != null
                && !item.getPackingItemName().isBlank()) {

            return item.getPackingItemName();
        }

        if (item.getItemName() != null
                && !item.getItemName().isBlank()) {

            return item.getItemName();
        }

        return String.valueOf(index);
    }


    // =========================================================
    // 화면 제품명
    // =========================================================
    private String getPackingItemName(
            DailyReportVo item
    ) {

        if (item.getPackingItemName() != null
                && !item.getPackingItemName().isBlank()) {

            return item.getPackingItemName();
        }

        return item.getItemName() == null
                ? ""
                : item.getItemName();
    }


    private String safeString(Object value) {

        return value == null
                ? ""
                : String.valueOf(value);
    }


    private double toDouble(Number value) {

        return value == null
                ? 0D
                : value.doubleValue();
    }


    // =========================================================
    // 제품별 부자재 사용량 내부 그룹
    // =========================================================
    private static class UseGroup {

        private final List<DailyReportVo> rows =
                new ArrayList<>();
    }
}
