package com.jct.mes_new.biz.base.excel;

import com.jct.mes_new.biz.base.mapper.DailyReportMapper;
import com.jct.mes_new.biz.base.mapper.LaborCostsMapper;
import com.jct.mes_new.biz.base.vo.DailyLaborCostVo;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import com.jct.mes_new.biz.base.vo.LaborCostRequestVo;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LaborCostExcel {

    private final LaborCostsMapper laborCostsMapper;
    private final DailyReportMapper dailyReportMapper;


    public byte[] download(DailyReportVo vo) {

        Workbook workbook = ExcelStyleUtil.createWorkbook();
        LaborCostRequestVo reqVo = new LaborCostRequestVo();

        try {

            // =========================================================
            // 데이터 조회
            // =========================================================
            Long dailyId = vo.getDailyId();

            reqVo.setDailyReportInfo(
                    dailyReportMapper.getDailyReportMst(dailyId)
            );

            reqVo.setWeighList(
                    laborCostsMapper.getLaborCostProcList(
                            "PRC001",
                            dailyId
                    )
            );

            reqVo.setMatList(
                    laborCostsMapper.getLaborCostProcList(
                            "PRC002",
                            dailyId
                    )
            );

            reqVo.setCoatingList(
                    laborCostsMapper.getLaborCostProcList(
                            "PRC003",
                            dailyId
                    )
            );

            reqVo.setChargeList(
                    laborCostsMapper.getLaborCostProcList(
                            "PRC004",
                            dailyId
                    )
            );

            reqVo.setPackingList(
                    laborCostsMapper.getLaborCostProcList(
                            "PRC005",
                            dailyId
                    )
            );


            // =========================================================
            // 마스터 정보
            // =========================================================
            DailyReportVo master =
                    reqVo.getDailyReportInfo();

            String dailyDate =
                    master != null
                            ? stringValue(master.getDailyDate())
                            : "";

            String areaCd =
                    master != null
                            ? master.getAreaCd()
                            : "";

            String workTypeCd =
                    master != null
                            ? master.getWorkTypeCd()
                            : "";


            String areaName = "";

            if ("A001".equals(areaCd)) {

                areaName = "시흥";

            } else if ("A002".equals(areaCd)) {

                areaName = "안산";

            } else {

                areaName = "기타";
            }


            String workTypeName = "";

            if ("D".equals(workTypeCd)) {

                workTypeName = "주간";

            } else if ("O".equals(workTypeCd)) {

                workTypeName = "잔업";

            } else if ("N".equals(workTypeCd)) {

                workTypeName = "야간";
            }


            String monthDay =
                    getMonthDay(dailyDate);


            // =========================================================
            // 시트명
            //
            // 예)
            // 인건비현황_시흥_주간_10월01일
            // =========================================================
            String sheetName = areaName +"_"+workTypeName+"_"+monthDay;


            // Excel 시트명 최대 31자
            if (sheetName.length() > 31) {

                sheetName =
                        sheetName.substring(
                                0,
                                31
                        );
            }


            // =========================================================
            // Sheet
            // =========================================================
            Sheet sheet =
                    workbook.createSheet(sheetName);

            sheet.setDisplayGridlines(false);


            // =========================================================
            // 인쇄 설정
            // =========================================================
            PrintSetup printSetup =
                    sheet.getPrintSetup();

            printSetup.setLandscape(true);
            printSetup.setFitWidth((short) 1);
            printSetup.setFitHeight((short) 0);

            sheet.setFitToPage(true);

            sheet.setMargin(
                    Sheet.LeftMargin,
                    0.2
            );

            sheet.setMargin(
                    Sheet.RightMargin,
                    0.2
            );

            sheet.setMargin(
                    Sheet.TopMargin,
                    0.3
            );

            sheet.setMargin(
                    Sheet.BottomMargin,
                    0.3
            );


            // =========================================================
            // 컬럼
            //
            // A  공정구분
            // B  LOT 및 제조번호
            // C  업체명
            // D  제품명
            // E  구분
            // F  생산수량
            // G  작업시간
            // H  남 정규직
            // I  남 일용직
            // J  여 정규직
            // K  여 일용직
            // L  인건비 합계
            // M  비고
            // =========================================================
            sheet.setColumnWidth(0, 13 * 256);
            sheet.setColumnWidth(1, 24 * 256);
            sheet.setColumnWidth(2, 26 * 256);
            sheet.setColumnWidth(3, 65 * 256);
            sheet.setColumnWidth(4, 12 * 256);
            sheet.setColumnWidth(5, 13 * 256);
            sheet.setColumnWidth(6, 11 * 256);
            sheet.setColumnWidth(7, 10 * 256);
            sheet.setColumnWidth(8, 10 * 256);
            sheet.setColumnWidth(9, 10 * 256);
            sheet.setColumnWidth(10, 10 * 256);
            sheet.setColumnWidth(11, 17 * 256);
            sheet.setColumnWidth(12, 28 * 256);


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
            // 폰트
            // =========================================================
            ExcelStyleUtil.applyFont(
                    workbook,
                    headerStyle,
                    "굴림",
                    (short) 10
            );

            ExcelStyleUtil.applyFont(
                    workbook,
                    leftStyle,
                    "굴림",
                    (short) 9
            );

            ExcelStyleUtil.applyFont(
                    workbook,
                    centerStyle,
                    "굴림",
                    (short) 9
            );

            ExcelStyleUtil.applyFont(
                    workbook,
                    numberStyle,
                    "굴림",
                    (short) 9
            );

            ExcelStyleUtil.applyFont(
                    workbook,
                    summaryStyle,
                    "굴림",
                    (short) 10
            );


            // =========================================================
            // 제목 스타일
            // =========================================================
            CellStyle reportTitleStyle =
                    workbook.createCellStyle();

            reportTitleStyle.cloneStyleFrom(
                    titleStyle
            );

            reportTitleStyle.setAlignment(
                    HorizontalAlignment.LEFT
            );

            reportTitleStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            Font titleFont =
                    workbook.createFont();

            titleFont.setFontName("굴림");
            titleFont.setFontHeightInPoints((short) 24);
            titleFont.setBold(false);

            reportTitleStyle.setFont(
                    titleFont
            );


            // =========================================================
            // 상단 일자 라벨 스타일
            //
            // 테두리 없음
            // =========================================================
            CellStyle dateLabelStyle =
                    workbook.createCellStyle();

            dateLabelStyle.setAlignment(
                    HorizontalAlignment.CENTER
            );

            dateLabelStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            Font dateLabelFont =
                    workbook.createFont();

            dateLabelFont.setFontName("굴림");
            dateLabelFont.setFontHeightInPoints((short) 10);
            dateLabelFont.setBold(true);

            dateLabelStyle.setFont(
                    dateLabelFont
            );


            // =========================================================
            // 상단 일자 값 스타일
            //
            // 테두리 없음
            // =========================================================
            CellStyle dateValueStyle =
                    workbook.createCellStyle();

            dateValueStyle.setAlignment(
                    HorizontalAlignment.CENTER
            );

            dateValueStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            Font dateValueFont =
                    workbook.createFont();

            dateValueFont.setFontName("굴림");
            dateValueFont.setFontHeightInPoints((short) 10);
            dateValueFont.setBold(false);

            dateValueStyle.setFont(
                    dateValueFont
            );


            // =========================================================
            // 헤더 스타일
            // =========================================================
            CellStyle mainHeaderStyle =
                    workbook.createCellStyle();

            mainHeaderStyle.cloneStyleFrom(
                    headerStyle
            );

            mainHeaderStyle.setAlignment(
                    HorizontalAlignment.CENTER
            );

            mainHeaderStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            mainHeaderStyle.setWrapText(true);


            // =========================================================
            // 숫자 스타일
            // =========================================================
            CellStyle qtyStyle =
                    workbook.createCellStyle();

            qtyStyle.cloneStyleFrom(
                    numberStyle
            );

            qtyStyle.setDataFormat(
                    workbook
                            .createDataFormat()
                            .getFormat("#,##0.##")
            );


            CellStyle moneyStyle =
                    workbook.createCellStyle();

            moneyStyle.cloneStyleFrom(
                    numberStyle
            );

            moneyStyle.setDataFormat(
                    workbook
                            .createDataFormat()
                            .getFormat("#,##0")
            );


            // =========================================================
            // 합계 스타일
            // =========================================================
            CellStyle summaryCenterStyle =
                    workbook.createCellStyle();

            summaryCenterStyle.cloneStyleFrom(
                    summaryStyle
            );

            summaryCenterStyle.setAlignment(
                    HorizontalAlignment.CENTER
            );

            summaryCenterStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );


            CellStyle summaryQtyStyle =
                    workbook.createCellStyle();

            summaryQtyStyle.cloneStyleFrom(
                    summaryStyle
            );

            summaryQtyStyle.setAlignment(
                    HorizontalAlignment.RIGHT
            );

            summaryQtyStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            summaryQtyStyle.setDataFormat(
                    workbook
                            .createDataFormat()
                            .getFormat("#,##0.##")
            );


            CellStyle summaryMoneyStyle =
                    workbook.createCellStyle();

            summaryMoneyStyle.cloneStyleFrom(
                    summaryStyle
            );

            summaryMoneyStyle.setAlignment(
                    HorizontalAlignment.RIGHT
            );

            summaryMoneyStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            summaryMoneyStyle.setDataFormat(
                    workbook
                            .createDataFormat()
                            .getFormat("#,##0")
            );


            // =========================================================
            // 공정 구분 스타일
            // =========================================================
            CellStyle procStyle =
                    workbook.createCellStyle();

            procStyle.cloneStyleFrom(
                    centerStyle
            );

            procStyle.setAlignment(
                    HorizontalAlignment.CENTER
            );

            procStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            procStyle.setWrapText(true);

            Font procFont =
                    workbook.createFont();

            procFont.setFontName("굴림");
            procFont.setFontHeightInPoints((short) 10);
            procFont.setBold(true);

            procStyle.setFont(
                    procFont
            );


            int rowNum = 0;


            // =========================================================
            // 제목
            // =========================================================
            Row titleRow =
                    ExcelStyleUtil.getRow(
                            sheet,
                            rowNum
                    );

            titleRow.setHeightInPoints(40);

            ExcelStyleUtil.setCellValue(
                    titleRow,
                    0,
                    "인 건 비 현 황 (" + areaName + ")",
                    reportTitleStyle
            );

            ExcelStyleUtil.mergeAndStyle(
                    sheet,
                    rowNum,
                    rowNum,
                    0,
                    12,
                    reportTitleStyle
            );


            // =========================================================
            // 제목 다음 행
            // =========================================================
            rowNum++;


            // =========================================================
            // 일자만 표시
            //
            // 구역 / 작업구분 제거
            // 테두리 없음
            // =========================================================
            Row dateRow =
                    ExcelStyleUtil.getRow(
                            sheet,
                            rowNum
                    );

            dateRow.setHeightInPoints(22);


            // 일자 라벨
            ExcelStyleUtil.setCellValue(
                    dateRow,
                    10,
                    "일자",
                    dateLabelStyle
            );


            // 날짜
            ExcelStyleUtil.setCellValue(
                    dateRow,
                    11,
                    dailyDate,
                    dateValueStyle
            );


            // 날짜 영역 L:M 병합
            sheet.addMergedRegion(
                    new CellRangeAddress(
                            rowNum,
                            rowNum,
                            11,
                            12
                    )
            );


            // 병합된 M열 스타일 적용
            Cell dateMergeCell =
                    dateRow.getCell(12);

            if (dateMergeCell == null) {

                dateMergeCell =
                        dateRow.createCell(12);
            }

            dateMergeCell.setCellStyle(
                    dateValueStyle
            );


            rowNum++;


            // =========================================================
            // Header
            // 2단 헤더
            // =========================================================
            rowNum = createHeader(
                    sheet,
                    rowNum,
                    mainHeaderStyle
            );


            // =========================================================
            // 1. 칭량
            // =========================================================
            rowNum = createSection(
                    sheet,
                    rowNum,
                    "칭량\n(kg)",
                    reqVo.getWeighList(),
                    procStyle,
                    leftStyle,
                    centerStyle,
                    qtyStyle,
                    moneyStyle,
                    summaryCenterStyle,
                    summaryQtyStyle,
                    summaryMoneyStyle
            );


            // =========================================================
            // 2. 제조
            // =========================================================
            rowNum = createSection(
                    sheet,
                    rowNum,
                    "제조\n(kg)",
                    reqVo.getMatList(),
                    procStyle,
                    leftStyle,
                    centerStyle,
                    qtyStyle,
                    moneyStyle,
                    summaryCenterStyle,
                    summaryQtyStyle,
                    summaryMoneyStyle
            );


            // =========================================================
            // 3. 코팅
            // =========================================================
            rowNum = createSection(
                    sheet,
                    rowNum,
                    "코팅\n(M)",
                    reqVo.getCoatingList(),
                    procStyle,
                    leftStyle,
                    centerStyle,
                    qtyStyle,
                    moneyStyle,
                    summaryCenterStyle,
                    summaryQtyStyle,
                    summaryMoneyStyle
            );


            // =========================================================
            // 4. 충전
            // =========================================================
            rowNum = createSection(
                    sheet,
                    rowNum,
                    "충전\n(EA)",
                    reqVo.getChargeList(),
                    procStyle,
                    leftStyle,
                    centerStyle,
                    qtyStyle,
                    moneyStyle,
                    summaryCenterStyle,
                    summaryQtyStyle,
                    summaryMoneyStyle
            );


            // =========================================================
            // 5. 포장
            // =========================================================
            createSection(
                    sheet,
                    rowNum,
                    "포장\n(EA)",
                    reqVo.getPackingList(),
                    procStyle,
                    leftStyle,
                    centerStyle,
                    qtyStyle,
                    moneyStyle,
                    summaryCenterStyle,
                    summaryQtyStyle,
                    summaryMoneyStyle
            );


            return ExcelStyleUtil.toByteArray(
                    workbook
            );

        } catch (Exception e) {

            log.error(
                    "인건비 현황 엑셀 생성 오류",
                    e
            );

            throw new RuntimeException(
                    "인건비 현황 엑셀 생성 중 오류가 발생했습니다.",
                    e
            );

        } finally {

            ExcelStyleUtil.clearStyleCache(
                    workbook
            );

            try {

                workbook.close();

            } catch (Exception ignored) {
            }
        }
    }


    // =========================================================
    // Header
    // =========================================================
    private int createHeader(
            Sheet sheet,
            int rowNum,
            CellStyle headerStyle
    ) {

        int topRowNum =
                rowNum;

        int bottomRowNum =
                rowNum + 1;


        Row topRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        topRowNum
                );

        Row bottomRow =
                ExcelStyleUtil.getRow(
                        sheet,
                        bottomRowNum
                );


        topRow.setHeightInPoints(24);
        bottomRow.setHeightInPoints(24);


        // =========================================================
        // 세로 병합 헤더
        // =========================================================
        setMergedVerticalHeader(
                sheet,
                topRowNum,
                bottomRowNum,
                0,
                "구 분",
                headerStyle
        );

        setMergedVerticalHeader(
                sheet,
                topRowNum,
                bottomRowNum,
                1,
                "LOT 및\n제조번호",
                headerStyle
        );

        setMergedVerticalHeader(
                sheet,
                topRowNum,
                bottomRowNum,
                2,
                "업체명",
                headerStyle
        );

        setMergedVerticalHeader(
                sheet,
                topRowNum,
                bottomRowNum,
                3,
                "제품명",
                headerStyle
        );

        setMergedVerticalHeader(
                sheet,
                topRowNum,
                bottomRowNum,
                4,
                "구분",
                headerStyle
        );

        setMergedVerticalHeader(
                sheet,
                topRowNum,
                bottomRowNum,
                5,
                "생산수량",
                headerStyle
        );

        setMergedVerticalHeader(
                sheet,
                topRowNum,
                bottomRowNum,
                6,
                "작업시간",
                headerStyle
        );


        // =========================================================
        // 남
        // =========================================================
        ExcelStyleUtil.setCellValue(
                topRow,
                7,
                "남",
                headerStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                topRowNum,
                topRowNum,
                7,
                8,
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                bottomRow,
                7,
                "정규직",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                bottomRow,
                8,
                "일용직",
                headerStyle
        );


        // =========================================================
        // 여
        // =========================================================
        ExcelStyleUtil.setCellValue(
                topRow,
                9,
                "여",
                headerStyle
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                topRowNum,
                topRowNum,
                9,
                10,
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                bottomRow,
                9,
                "정규직",
                headerStyle
        );

        ExcelStyleUtil.setCellValue(
                bottomRow,
                10,
                "일용직",
                headerStyle
        );


        setMergedVerticalHeader(
                sheet,
                topRowNum,
                bottomRowNum,
                11,
                "인건비\n합계",
                headerStyle
        );

        setMergedVerticalHeader(
                sheet,
                topRowNum,
                bottomRowNum,
                12,
                "비고",
                headerStyle
        );


        return rowNum + 2;
    }


    // =========================================================
    // 세로 병합 Header
    // =========================================================
    private void setMergedVerticalHeader(
            Sheet sheet,
            int firstRow,
            int lastRow,
            int col,
            String value,
            CellStyle style
    ) {

        Row row =
                ExcelStyleUtil.getRow(
                        sheet,
                        firstRow
                );

        ExcelStyleUtil.setCellValue(
                row,
                col,
                value,
                style
        );

        ExcelStyleUtil.mergeAndStyle(
                sheet,
                firstRow,
                lastRow,
                col,
                col,
                style
        );
    }


    // =========================================================
    // 공정별 Data
    // =========================================================
    private int createSection(
            Sheet sheet,
            int rowNum,
            String procName,
            List<DailyLaborCostVo> list,
            CellStyle procStyle,
            CellStyle leftStyle,
            CellStyle centerStyle,
            CellStyle qtyStyle,
            CellStyle moneyStyle,
            CellStyle summaryCenterStyle,
            CellStyle summaryQtyStyle,
            CellStyle summaryMoneyStyle
    ) {

        int startRow =
                rowNum;

        double totalQty =
                0D;

        double totalLaborCost =
                0D;


        // =========================================================
        // 데이터가 없을 경우
        // =========================================================
        if (
                list == null
                        || list.isEmpty()
        ) {

            Row row =
                    ExcelStyleUtil.getRow(
                            sheet,
                            rowNum
                    );

            row.setHeightInPoints(23);


            ExcelStyleUtil.setCellValue(
                    row,
                    0,
                    procName,
                    procStyle
            );


            for (
                    int col = 1;
                    col <= 12;
                    col++
            ) {

                ExcelStyleUtil.setCellValue(
                        row,
                        col,
                        "",
                        col == 3
                                ? leftStyle
                                : centerStyle
                );
            }


            rowNum++;


            Row summaryRow =
                    ExcelStyleUtil.getRow(
                            sheet,
                            rowNum
                    );

            summaryRow.setHeightInPoints(23);


            ExcelStyleUtil.setCellValue(
                    summaryRow,
                    0,
                    "소 계",
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
                    0D,
                    summaryQtyStyle
            );


            for (
                    int col = 6;
                    col <= 10;
                    col++
            ) {

                ExcelStyleUtil.setCellValue(
                        summaryRow,
                        col,
                        "",
                        summaryCenterStyle
                );
            }


            ExcelStyleUtil.setNumberCell(
                    summaryRow,
                    11,
                    0D,
                    summaryMoneyStyle
            );

            ExcelStyleUtil.setCellValue(
                    summaryRow,
                    12,
                    "",
                    summaryCenterStyle
            );


            return rowNum + 1;
        }


        // =========================================================
        // 데이터
        // =========================================================
        for (
                DailyLaborCostVo item : list
        ) {

            Row row =
                    ExcelStyleUtil.getRow(
                            sheet,
                            rowNum
                    );

            row.setHeightInPoints(23);


            // =========================================================
            // 공정
            // =========================================================
            ExcelStyleUtil.setCellValue(
                    row,
                    0,
                    "",
                    procStyle
            );


            // =========================================================
            // LOT / 제조번호
            // =========================================================
            ExcelStyleUtil.setCellValue(
                    row,
                    1,
                    item.getLotNo(),
                    centerStyle
            );


            // =========================================================
            // 업체명
            // =========================================================
            ExcelStyleUtil.setCellValue(
                    row,
                    2,
                    item.getCustomerName(),
                    leftStyle
            );


            // =========================================================
            // 제품명
            // =========================================================
            ExcelStyleUtil.setCellValue(
                    row,
                    3,
                    item.getItemName(),
                    leftStyle
            );


            // =========================================================
            // 제품 유형
            // =========================================================
            ExcelStyleUtil.setCellValue(
                    row,
                    4,
                    item.getProdType(),
                    centerStyle
            );


            double prodQty =
                    toDouble(
                            item.getProdQty()
                    );

            double workTime =
                    toDouble(
                            item.getWorkTime()
                    );

            double manFCnt =
                    toDouble(
                            item.getManFCnt()
                    );

            double manDCnt =
                    toDouble(
                            item.getManDCnt()
                    );

            double womFCnt =
                    toDouble(
                            item.getWomFCnt()
                    );

            double womDCnt =
                    toDouble(
                            item.getWomDCnt()
                    );


            // =========================================================
            // 생산수량
            // =========================================================
            ExcelStyleUtil.setNumberCell(
                    row,
                    5,
                    prodQty,
                    qtyStyle
            );


            // =========================================================
            // 작업시간
            // =========================================================
            ExcelStyleUtil.setNumberCell(
                    row,
                    6,
                    workTime,
                    qtyStyle
            );


            // =========================================================
            // 인원
            // =========================================================
            ExcelStyleUtil.setNumberCell(
                    row,
                    7,
                    manFCnt,
                    qtyStyle
            );

            ExcelStyleUtil.setNumberCell(
                    row,
                    8,
                    manDCnt,
                    qtyStyle
            );

            ExcelStyleUtil.setNumberCell(
                    row,
                    9,
                    womFCnt,
                    qtyStyle
            );

            ExcelStyleUtil.setNumberCell(
                    row,
                    10,
                    womDCnt,
                    qtyStyle
            );


            // =========================================================
            // 인건비
            // =========================================================
            double laborCost =
                    calculateLaborCost(item);


            ExcelStyleUtil.setNumberCell(
                    row,
                    11,
                    laborCost,
                    moneyStyle
            );


            // =========================================================
            // 비고
            // =========================================================
            ExcelStyleUtil.setCellValue(
                    row,
                    12,
                    item.getEtc(),
                    leftStyle
            );


            totalQty +=
                    prodQty;

            totalLaborCost +=
                    laborCost;


            rowNum++;
        }


        // =========================================================
        // 공정명 세로 병합
        // =========================================================
        if (
                rowNum > startRow
        ) {

            Row firstRow =
                    ExcelStyleUtil.getRow(
                            sheet,
                            startRow
                    );

            ExcelStyleUtil.setCellValue(
                    firstRow,
                    0,
                    procName,
                    procStyle
            );


            if (
                    rowNum - 1 > startRow
            ) {

                sheet.addMergedRegion(
                        new CellRangeAddress(
                                startRow,
                                rowNum - 1,
                                0,
                                0
                        )
                );


                for (
                        int r = startRow;
                        r < rowNum;
                        r++
                ) {

                    Row mergeRow =
                            ExcelStyleUtil.getRow(
                                    sheet,
                                    r
                            );

                    Cell cell =
                            mergeRow.getCell(0);

                    if (
                            cell == null
                    ) {

                        cell =
                                mergeRow.createCell(0);
                    }

                    cell.setCellStyle(
                            procStyle
                    );
                }
            }
        }


        // =========================================================
        // 소계
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
                "소 계",
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
                summaryQtyStyle
        );


        for (
                int col = 6;
                col <= 10;
                col++
        ) {

            ExcelStyleUtil.setCellValue(
                    summaryRow,
                    col,
                    "",
                    summaryCenterStyle
            );
        }


        ExcelStyleUtil.setNumberCell(
                summaryRow,
                11,
                totalLaborCost,
                summaryMoneyStyle
        );


        ExcelStyleUtil.setCellValue(
                summaryRow,
                12,
                "",
                summaryCenterStyle
        );


        return rowNum + 1;
    }


    // =========================================================
    // 인건비 계산
    //
    // 남 정규
    // 남 일용
    // 여 정규
    // 여 일용
    // =========================================================
    private double calculateLaborCost(
            DailyLaborCostVo item
    ) {

        double workTime =
                toDouble(
                        item.getWorkTime()
                );

        double manFCost =
                toDouble(
                        item.getManFCost()
                );

        double manDCost =
                toDouble(
                        item.getManDCost()
                );

        double womFCost =
                toDouble(
                        item.getWomFCost()
                );

        double womDCost =
                toDouble(
                        item.getWomDCost()
                );


        return
                (
                        toDouble(
                                item.getManFCnt()
                        )
                                * workTime
                                * manFCost
                )
                        +
                        (
                                toDouble(
                                        item.getManDCnt()
                                )
                                        * workTime
                                        * manDCost
                        )
                        +
                        (
                                toDouble(
                                        item.getWomFCnt()
                                )
                                        * workTime
                                        * womFCost
                        )
                        +
                        (
                                toDouble(
                                        item.getWomDCnt()
                                )
                                        * workTime
                                        * womDCost
                        );
    }


    // =========================================================
    // 근무구분
    // =========================================================
    private String getWorkTypeName(
            String workTypeCd
    ) {

        if (
                workTypeCd == null
        ) {

            return "";
        }


        return switch (
                workTypeCd
                ) {

            case "D" ->
                    "주간";

            case "O" ->
                    "잔업";

            case "N" ->
                    "야간";

            default ->
                    workTypeCd;
        };
    }


    private double toDouble(
            Number value
    ) {

        return value == null
                ? 0D
                : value.doubleValue();
    }


    private String stringValue(
            Object value
    ) {

        return value == null
                ? ""
                : String.valueOf(value);
    }


    // =========================================================
    // 날짜
    //
    // 2026-10-01
    // ->
    // 10월01일
    // =========================================================
    private String getMonthDay(
            String date
    ) {

        if (
                date == null
                        || date.isBlank()
        ) {

            return "";
        }


        try {

            LocalDate localDate =
                    LocalDate.parse(
                            date.substring(
                                    0,
                                    10
                            )
                    );


            return String.format(
                    "%02d%02d",
                    localDate.getMonthValue(),
                    localDate.getDayOfMonth()
            );

        } catch (Exception e) {

            return date;
        }
    }
}