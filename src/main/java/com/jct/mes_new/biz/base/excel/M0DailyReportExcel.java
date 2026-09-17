package com.jct.mes_new.biz.base.excel;

import com.jct.mes_new.biz.base.mapper.M0DailyReportMapper;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class M0DailyReportExcel {

    private final M0DailyReportMapper m0DailyReportMapper;


    public byte[] download(DailyReportVo vo) {

        Workbook workbook = ExcelStyleUtil.createWorkbook();

        try {

            Sheet sheet =
                    workbook.createSheet("완제품 일일 외주 및 생산내역");

            sheet.setDisplayGridlines(false);


            // =========================================================
            // 컬럼 폭
            // =========================================================
            sheet.setColumnWidth(0, 13 * 256);   // 일자
            sheet.setColumnWidth(1, 5 * 256);    // NO
            sheet.setColumnWidth(2, 15 * 256);   // 품목코드
            sheet.setColumnWidth(3, 34 * 256);   // 거래처명
            // 품명 크게 확대
            sheet.setColumnWidth(4, 80 * 256);   // 품명
            sheet.setColumnWidth(5, 30 * 256);   // LOT
            sheet.setColumnWidth(6, 14 * 256);   // 수량
            sheet.setColumnWidth(7, 14 * 256);   // 단가
            sheet.setColumnWidth(8, 17 * 256);   // 공급가액
            sheet.setColumnWidth(9, 15 * 256);   // 창고 / 비고
            sheet.setColumnWidth(10, 24 * 256);  // 비고

            // =========================================================
            // 기본 스타일
            // =========================================================
            CellStyle titleStyle =
                    ExcelStyleUtil.getReportTitleStyle(workbook);

            CellStyle headerStyle =
                    ExcelStyleUtil.getHeaderStyle(workbook);

            CellStyle leftStyle =
                    ExcelStyleUtil.getBorderStyle(
                            workbook,
                            "LEFT"
                    );

            CellStyle centerStyle =
                    ExcelStyleUtil.getBorderStyle(
                            workbook,
                            "CENTER"
                    );

            CellStyle numberStyle =
                    ExcelStyleUtil.getReportNumberStyle(workbook);

            CellStyle summaryStyle =
                    ExcelStyleUtil.getReportSummaryStyle(workbook);


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
            CellStyle integerStyle =
                    workbook.createCellStyle();

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
            // 합계 제목
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


            // =========================================================
            // 1. 생산
            // =========================================================
            report.setTranTypeCd("A");

            List<DailyReportVo> inList =
                    m0DailyReportMapper.getDailyReportList(report);


            // =========================================================
            // 2. 외주 생산
            // =========================================================
            report.setTranTypeCd("O");

            List<DailyReportVo> outList =
                    m0DailyReportMapper.getDailyReportList(report);


            // =========================================================
            // 3. 외주 생산 비용
            // =========================================================
            report.setTranTypeCd("E");

            List<DailyReportVo> outExpenseList =
                    m0DailyReportMapper.getDailyReportList(report);


            // =========================================================
            // 4. 불량 및 폐기
            // =========================================================
            report.setTranTypeCd("G");

            List<DailyReportVo> discardList =
                    m0DailyReportMapper.getDailyReportList(report);


            // =========================================================
            // 5. 출하
            // =========================================================
            report.setTranTypeCd("F");

            List<DailyReportVo> shipmentList =
                    m0DailyReportMapper.getDailyReportList(report);


            // =========================================================
            // 6. 반품
            // =========================================================
            report.setTranTypeCd("P");

            List<DailyReportVo> returnList =
                    m0DailyReportMapper.getDailyReportList(report);


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
                    "완제품 일일 외주 및 생산내역",
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
            // 1. 당일 완제품 생산 내역
            //
            // 창고 없음
            // 비고 9~10 병합
            // =========================================================
            rowNum = createM0Section(
                    workbook,
                    sheet,
                    rowNum,
                    "1. 당일 완제품 생산 내역",
                    "생산일자",
                    "",
                    inList,
                    IndexedColors.LIGHT_CORNFLOWER_BLUE,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle,
                    false
            );

            rowNum++;


            // =========================================================
            // 2. 외주 생산
            // =========================================================
            rowNum = createM0Section(
                    workbook,
                    sheet,
                    rowNum,
                    "2. 당일 완제품 외주 생산 내역(판매단가)",
                    "입고일자",
                    "입고창고",
                    outList,
                    IndexedColors.LIGHT_GREEN,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle,
                    true
            );

            rowNum++;


            // =========================================================
            // 3. 외주 생산 비용
            // =========================================================
            rowNum = createM0Section(
                    workbook,
                    sheet,
                    rowNum,
                    "3. 당일 완제품 외주 생산 비용(외주단가)",
                    "입고일자",
                    "입고창고",
                    outExpenseList,
                    IndexedColors.LIGHT_GREEN,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle,
                    true
            );

            rowNum++;


            // =========================================================
            // 4. 불량 및 폐기
            // =========================================================
            rowNum = createM0DiscardSection(
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
            // 5. 출하
            // =========================================================
            rowNum = createM0Section(
                    workbook,
                    sheet,
                    rowNum,
                    "5. 당일 완제품 출하 내역",
                    "출하일자",
                    "입고창고",
                    shipmentList,
                    IndexedColors.LIGHT_ORANGE,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle,
                    true
            );

            rowNum++;


            // =========================================================
            // 6. 반품
            // =========================================================
            createM0Section(
                    workbook,
                    sheet,
                    rowNum,
                    "6. 당일 완제품 반품 내역",
                    "반품일자",
                    "입고창고",
                    returnList,
                    IndexedColors.LIGHT_ORANGE,
                    headerStyle,
                    leftStyle,
                    centerStyle,
                    integerStyle,
                    summaryCenterStyle,
                    summaryNumberStyle,
                    true
            );


            return ExcelStyleUtil.toByteArray(workbook);

        } catch (Exception e) {

            throw new RuntimeException(
                    "완제품 일일보고서 엑셀 생성 중 오류가 발생했습니다.",
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
    // 생산 / 외주 / 외주비용 / 출하 / 반품 공통
    // =========================================================
    private int createM0Section(
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
            CellStyle summaryNumberStyle,
            boolean showStorage
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
        Row headerRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        rowNum
                );

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
                "LOT",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                6,
                "수량[EA]",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                7,
                "단가",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                8,
                "공급가액",
                headerStyle
        );


        if (showStorage) {

            // 창고 존재
            ExcelStyleUtil.setCellValue(
                    headerRow,
                    9,
                    storageHeader,
                    headerStyle
            );

            ExcelStyleUtil.setCellValue(
                    headerRow,
                    10,
                    "비고",
                    headerStyle
            );

        } else {

            // 첫 번째 생산 목록
            // 빈 창고 컬럼 제거
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
        }


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


                // 소수점 없이 표시
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


                if (showStorage) {

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

                } else {

                    // 첫 번째 생산 목록 비고 9~10
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
                }


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


        if (showStorage) {

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

        } else {

            // 첫 번째 목록 비고 영역도 9~10 병합
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
        }


        return rowNum + 1;
    }


    // =========================================================
    // 4. 당일 완제품 불량 및 폐기 내역
    // LOT 포함
    // 비고 9~10 병합
    // =========================================================
    private int createM0DiscardSection(
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
                "4. 당일 완제품 불량 및 폐기 내역",
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
                "불량일자",
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
                "LOT",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                6,
                "수량[EA]",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                7,
                "단가",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                headerRow,
                8,
                "공급가액",
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