package com.jct.mes_new.biz.base.excel;

import com.jct.mes_new.biz.base.mapper.M1DailyReportMapper;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class M1DailyReportExcel {

    private final M1DailyReportMapper m1DailyReportMapper;


    public byte[] download(DailyReportVo vo) {

        Workbook workbook = ExcelStyleUtil.createWorkbook();

        try {

            Sheet sheet =
                    workbook.createSheet("원료 일일 입고 및 사용내역");

            sheet.setDisplayGridlines(false);


            // =========================================================
            // 컬럼 폭
            // 전체 리스트 공통 적용
            // =========================================================
            sheet.setColumnWidth(0, 13 * 256);   // 일자
            sheet.setColumnWidth(1, 6 * 256);    // NO
            sheet.setColumnWidth(2, 15 * 256);   // 품목코드
            sheet.setColumnWidth(3, 35 * 256);   // 거래처명
            sheet.setColumnWidth(4, 80 * 256);   // 제품명
            sheet.setColumnWidth(5, 20 * 256);   // 규격 / LOT / 소요량
            sheet.setColumnWidth(6, 15 * 256);   // 수량 / 단가
            sheet.setColumnWidth(7, 15 * 256);   // 단가 / 합계
            sheet.setColumnWidth(8, 15 * 256);   // 금액
            sheet.setColumnWidth(9, 15 * 256);   // 사용기한 / 구역 / 비고
            sheet.setColumnWidth(10, 24 * 256);  // 비고


            // =========================================================
            // 기본 스타일
            // =========================================================
            CellStyle titleStyle =
                    ExcelStyleUtil.getReportTitleStyle(workbook);

            CellStyle headerStyle =
                    ExcelStyleUtil.getHeaderStyle(workbook);

            CellStyle leftStyle =
                    ExcelStyleUtil.getBorderStyle(workbook, "LEFT");

            CellStyle centerStyle =
                    ExcelStyleUtil.getBorderStyle(workbook, "CENTER");

            CellStyle numberStyle =
                    ExcelStyleUtil.getReportNumberStyle(workbook);

            CellStyle summaryStyle =
                    ExcelStyleUtil.getReportSummaryStyle(workbook);


            // =========================================================
            // 폰트
            // 표 영역 굴림 13pt
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
            // 숫자 소수점 제거 스타일
            // #,##0
            // =========================================================
            CellStyle integerStyle =
                    workbook.createCellStyle();

            integerStyle.cloneStyleFrom(numberStyle);

            integerStyle.setDataFormat(
                    workbook
                            .createDataFormat()
                            .getFormat("#,##0")
            );


            // =========================================================
            // 합계 숫자 스타일
            // 소수점 제거 + 우측 정렬
            // =========================================================
            CellStyle summaryNumberStyle =
                    workbook.createCellStyle();

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
            // 합계 제목 스타일
            // 가운데 정렬
            // =========================================================
            CellStyle summaryCenterStyle =
                    workbook.createCellStyle();

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
            DailyReportVo report =
                    new DailyReportVo();

            report.setDailyId(
                    vo.getDailyId()
            );

            report.setItemTypeCd("M1");


            // 입고
            report.setTranTypeCd("A");

            List<DailyReportVo> inList =
                    m1DailyReportMapper.getDailyReportList(report);


            // 반품
            report.setTranTypeCd("P");

            List<DailyReportVo> returnList =
                    m1DailyReportMapper.getDailyReportList(report);


            // 불량
            report.setTranTypeCd("G");

            List<DailyReportVo> discardList =
                    m1DailyReportMapper.getDailyReportList(report);


            // 칭량
            List<DailyReportVo> prodList =
                    m1DailyReportMapper.getProdList(report);


            // 원료 사용량
            List<DailyReportVo> useList =
                    m1DailyReportMapper.getUseList(report);


            // 외부출고
            report.setTranTypeCd("Z");

            List<DailyReportVo> ospList =
                    m1DailyReportMapper.getDailyReportList(report);


            int rowNum = 0;


            // =========================================================
            // 제목
            // =========================================================
            Row titleRow =
                    ExcelStyleUtil.getRow(
                            sheet,
                            rowNum
                    );

            titleRow.setHeightInPoints(30);


            ExcelStyleUtil.setCellValue(
                    titleRow,
                    0,
                    "원료 일일 입고 및 사용내역",
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
            // 1. 원료 입고
            // =========================================================
            rowNum = createM1CommonSection(
                    workbook,
                    sheet,
                    rowNum,
                    "1. 원료 입고",
                    "입고일자",
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
            // 2. 원료 반품
            // =========================================================
            rowNum = createM1CommonSection(
                    workbook,
                    sheet,
                    rowNum,
                    "2. 원료 반품",
                    "반품일자",
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
            // 3. 원료 불량
            // =========================================================
            rowNum = createM1CommonSection(
                    workbook,
                    sheet,
                    rowNum,
                    "3. 원료 불량",
                    "불량일자",
                    discardList,
                    IndexedColors.GREY_25_PERCENT,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle
            );

            rowNum++;


            // =========================================================
            // 4. 칭량 제품
            // =========================================================
            rowNum = createM1ProdSection(
                    workbook,
                    sheet,
                    rowNum,
                    prodList,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle
            );

            rowNum++;


            // =========================================================
            // 5. 원료 사용량
            // =========================================================
            rowNum = createM1UseSection(
                    workbook,
                    sheet,
                    rowNum,
                    useList,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle
            );

            rowNum++;


            // =========================================================
            // 6. 원료 외주반출
            // =========================================================
            createM1CommonSection(
                    workbook,
                    sheet,
                    rowNum,
                    "6. 원료 외주반출",
                    "발송일자",
                    ospList,
                    IndexedColors.LIGHT_GREEN,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle
            );


            return ExcelStyleUtil.toByteArray(workbook);

        } catch (Exception e) {

            throw new RuntimeException(
                    "원료 일일보고서 엑셀 생성 중 오류가 발생했습니다.",
                    e
            );

        } finally {

            ExcelStyleUtil.clearStyleCache(workbook);

            try {
                workbook.close();
            } catch (Exception ignored) {
            }
        }
    }


    // =========================================================
    // 입고 / 반품 / 불량 / 외주반출 공통
    // =========================================================
    private int createM1CommonSection(
            Workbook workbook,
            Sheet sheet,
            int rowNum,
            String title,
            String dateHeader,
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


        Row sectionRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        rowNum
                );

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
        // Header
        // =========================================================
        String[] headers = {
                dateHeader,
                "NO",
                "품목코드",
                "거래처명",
                "제품명",
                "규격[kg]",
                "수량[kg]",
                "단가[원]",
                "공급가[원]",
                "사용기한",
                "비고"
        };


        ExcelStyleUtil.createHeader(
                sheet,
                rowNum++,
                headers,
                headerStyle
        );


        double totalQty = 0D;
        double totalAmount = 0D;


        // =========================================================
        // Data
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
                        centerStyle
                );


                ExcelStyleUtil.setCellValue(
                        row,
                        4,
                        item.getItemName(),
                        leftStyle
                );


                ExcelStyleUtil.setCellValue(
                        row,
                        5,
                        item.getSpec(),
                        centerStyle
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
                        6,
                        qty,
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
                        item.getExpiryDate(),
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


        // 합계 제목 - 가운데 정렬
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
                5,
                summaryCenterStyle
        );


        // 수량 합계
        ExcelStyleUtil.setNumberCell(
                summaryRow,
                6,
                totalQty,
                summaryNumberStyle
        );


        ExcelStyleUtil.setCellValue(
                summaryRow,
                7,
                "",
                summaryNumberStyle
        );


        // 금액 합계
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
    // 칭량 제품
    // =========================================================
    private int createM1ProdSection(
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
                        IndexedColors.LIGHT_YELLOW
                );


        ExcelStyleUtil.applyFont(
                workbook,
                sectionStyle,
                "굴림",
                (short) 13
        );


        Row sectionRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        rowNum
                );

        sectionRow.setHeightInPoints(24);


        ExcelStyleUtil.setCellValue(
                sectionRow,
                0,
                "4. 칭량 제품",
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


        String[] headers = {
                "칭량지시일",
                "NO",
                "품목코드",
                "거래처명",
                "품목명",
                "로트번호",
                "지시수량",
                "(g)당 단가",
                "합계",
                "구역",
                "비고"
        };


        ExcelStyleUtil.createHeader(
                sheet,
                rowNum++,
                headers,
                headerStyle
        );


        double totalQty = 0D;
        double totalAmount = 0D;


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
                        centerStyle
                );


                ExcelStyleUtil.setCellValue(
                        row,
                        4,
                        item.getItemName(),
                        leftStyle
                );


                ExcelStyleUtil.setCellValue(
                        row,
                        5,
                        item.getLotNo(),
                        centerStyle
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
                        6,
                        qty,
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
                        item.getAreaName(),
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
                5,
                summaryCenterStyle
        );


        ExcelStyleUtil.setNumberCell(
                summaryRow,
                6,
                totalQty,
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
    // 원료 사용량
    // =========================================================
    private int createM1UseSection(
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
                        IndexedColors.LIGHT_GREEN
                );


        ExcelStyleUtil.applyFont(
                workbook,
                sectionStyle,
                "굴림",
                (short) 13
        );


        Row sectionRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        rowNum
                );

        sectionRow.setHeightInPoints(24);


        ExcelStyleUtil.setCellValue(
                sectionRow,
                0,
                "5. 원료 사용량",
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
                "칭량지시일",
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
                "제품명",
                headerStyle
        );


        ExcelStyleUtil.setCellValue(
                headerRow,
                5,
                "소요량",
                headerStyle
        );


        ExcelStyleUtil.setCellValue(
                headerRow,
                6,
                "단가[원]",
                headerStyle
        );


        // 합계금액 7~8 병합
        ExcelStyleUtil.setCellValue(
                headerRow,
                7,
                "합계금액[원]",
                headerStyle
        );


        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                7,
                8,
                headerStyle
        );


        // 비고 9~10 병합
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
        // Data
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
                        centerStyle
                );


                ExcelStyleUtil.setCellValue(
                        row,
                        4,
                        item.getItemName(),
                        leftStyle
                );


                double requiredQty =
                        toDouble(
                                item.getRequiredQuantity()
                        );

                double inPrice =
                        toDouble(
                                item.getInPrice()
                        );

                double amount =
                        requiredQty * inPrice;


                ExcelStyleUtil.setNumberCell(
                        row,
                        5,
                        requiredQty,
                        numberStyle
                );


                ExcelStyleUtil.setNumberCell(
                        row,
                        6,
                        inPrice,
                        numberStyle
                );


                // 합계금액 7~8
                ExcelStyleUtil.setNumberCell(
                        row,
                        7,
                        amount,
                        numberStyle
                );


                ExcelStyleUtil.mergeAndStyle(
                        sheet,
                        rowNum,
                        rowNum,
                        7,
                        8,
                        numberStyle
                );


                // 비고 9~10
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


                totalQty += requiredQty;
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


        // 합계 가운데 정렬
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


        // 소요량 합계
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


        // 금액 합계 7~8
        ExcelStyleUtil.setNumberCell(
                summaryRow,
                7,
                totalAmount,
                summaryNumberStyle
        );


        ExcelStyleUtil.mergeAndStyle(
                sheet,
                rowNum,
                rowNum,
                7,
                8,
                summaryNumberStyle
        );


        // 비고 9~10
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


    private double toDouble(Number value) {

        return value == null
                ? 0D
                : value.doubleValue();
    }
}