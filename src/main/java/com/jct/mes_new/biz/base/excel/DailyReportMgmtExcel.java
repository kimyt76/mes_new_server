package com.jct.mes_new.biz.base.excel;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import com.jct.mes_new.biz.base.mapper.DailyMgmtMapper;
import com.jct.mes_new.biz.base.mapper.DailyReportMapper;
import com.jct.mes_new.biz.base.vo.DailyMgmtVo;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportMgmtExcel {

    private static final String TEMPLATE_PATH = "excel/daily_mgmt.xlsx";

    private static final DateTimeFormatter SHEET_NAME_FORMAT = DateTimeFormatter.ofPattern("MM.dd");

    private final DailyMgmtMapper dailyMgmtMapper;
    private final DailyReportMapper dailyReportMapper;

    public byte[] download(DailyReportVo vo) {

        Workbook workbook = null;

        try {
            DailyMgmtVo mgmt = dailyReportMapper.getDailyMgmtInfo(vo.getDailyId());

            if (mgmt == null) {
                throw new IllegalArgumentException(
                        "일일 손익자료가 존재하지 않습니다. dailyId="
                                + vo.getDailyId()
                );
            }

            ClassPathResource resource =
                    new ClassPathResource(TEMPLATE_PATH);

            try (InputStream inputStream =
                         resource.getInputStream()) {

                // ExcelStyleUtil에 이미 있는 템플릿 Workbook 생성 함수 사용
                workbook =
                        ExcelStyleUtil.createWorkbook(
                                inputStream
                        );
            }

            removeOtherSheets(workbook);

            Sheet sheet = workbook.getSheetAt(0);

            LocalDate dailyDate = mgmt.getDailyDate();

            if (dailyDate != null) {
                workbook.setSheetName(
                        0,
                        dailyDate.format(SHEET_NAME_FORMAT)
                );

                setDate(
                        sheet,
                        "N7",
                        dailyDate
                );
            }

            // 템플릿의 예시 입력값만 제거하고 계산 수식은 유지
            clearTemplateValues(sheet);

            // 1~3. 원료, 부자재, 완제품 당일 재고 현황
            setNumber(sheet, "B10", mgmt.getRawPrevStockQty());
            setNumber(sheet, "B11", mgmt.getRawPrevStockAmt());
            setNumber(sheet, "C10", mgmt.getRawReceiptQty());
            setNumber(sheet, "C11", mgmt.getRawReceiptAmt());
            setNumber(sheet, "E10", mgmt.getRawReturnQty());
            setNumber(sheet, "E11", mgmt.getRawReturnAmt());
            setNumber(sheet, "G10", mgmt.getRawDefectQty());
            setNumber(sheet, "G11", mgmt.getRawDefectAmt());
            setNumber(sheet, "I10", mgmt.getRawOutsourceQty());
            setNumber(sheet, "I11", mgmt.getRawOutsourceAmt());
            setNumber(sheet, "J10", mgmt.getRawUsageQty());
            setNumber(sheet, "J11", mgmt.getRawUsageAmt());
            setNumber(sheet, "K10", mgmt.getRawOutboundQty());
            setNumber(sheet, "K11", mgmt.getRawOutboundAmt());
            setNumber(sheet, "B15", mgmt.getSubPrevStockQty());
            setNumber(sheet, "B16", mgmt.getSubPrevStockAmt());
            setNumber(sheet, "C15", mgmt.getSubReceiptQty());
            setNumber(sheet, "C16", mgmt.getSubReceiptAmt());
            setNumber(sheet, "E15", mgmt.getSubReturnQty());
            setNumber(sheet, "E16", mgmt.getSubReturnAmt());
            setNumber(sheet, "G15", mgmt.getSubDefectQty());
            setNumber(sheet, "G16", mgmt.getSubDefectAmt());
            setNumber(sheet, "I15", mgmt.getSubOutsourceQty());
            setNumber(sheet, "I16", mgmt.getSubOutsourceAmt());
            setNumber(sheet, "J15", mgmt.getSubUsageQty());
            setNumber(sheet, "J16", mgmt.getSubUsageAmt());
            setNumber(sheet, "K15", mgmt.getSubOutboundQty());
            setNumber(sheet, "K16", mgmt.getSubOutboundAmt());
            setNumber(sheet, "B20", mgmt.getProductPrevStockQty());
            setNumber(sheet, "B21", mgmt.getProductPrevStockAmt());
            setNumber(sheet, "C20", mgmt.getProductProdQty());
            setNumber(sheet, "C21", mgmt.getProductProdAmt());
            setNumber(sheet, "E20", mgmt.getProductOutsourceProdQty());
            setNumber(sheet, "E21", mgmt.getProductOutsourceProdAmt());
            setNumber(sheet, "G20", mgmt.getProductDefectQty());
            setNumber(sheet, "G21", mgmt.getProductDefectAmt());
            setNumber(sheet, "I20", mgmt.getProductOutsourceQty());
            setNumber(sheet, "I21", mgmt.getProductOutsourceAmt());
            setNumber(sheet, "J20", mgmt.getProductShipmentQty());
            setNumber(sheet, "J21", mgmt.getProductShipmentAmt());
            setNumber(sheet, "K20", mgmt.getProductReturnQty());
            setNumber(sheet, "K21", mgmt.getProductReturnAmt());

            // 4. 당일 인건비 현황 (26~37행)
            setNumber(sheet, "B26", mgmt.getMgmtDayMaleCnt());
            setNumber(sheet, "C26", mgmt.getMgmtDayFemaleCnt());
            setNumber(sheet, "G26", mgmt.getMgmtDayWorkHour());
            setNumber(sheet, "B27", mgmt.getRegularDayMaleCnt());
            setNumber(sheet, "C27", mgmt.getRegularDayFemaleCnt());
            setNumber(sheet, "G27", mgmt.getRegularDayWorkHour());
            setNumber(sheet, "B28", mgmt.getDailyMaleDayMaleCnt());
            setNumber(sheet, "C28", mgmt.getDailyMaleDayFemaleCnt());
            setNumber(sheet, "G28", mgmt.getDailyMaleDayWorkHour());
            setNumber(sheet, "B29", mgmt.getDailyFemaleDayMaleCnt());
            setNumber(sheet, "C29", mgmt.getDailyFemaleDayFemaleCnt());
            setNumber(sheet, "G29", mgmt.getDailyFemaleDayWorkHour());
            setNumber(sheet, "B30", mgmt.getMgmtOvertimeMaleCnt());
            setNumber(sheet, "C30", mgmt.getMgmtOvertimeFemaleCnt());
            setNumber(sheet, "G30", mgmt.getMgmtOvertimeWorkHour());
            setNumber(sheet, "B31", mgmt.getRegularOvertimeMaleCnt());
            setNumber(sheet, "C31", mgmt.getRegularOvertimeFemaleCnt());
            setNumber(sheet, "G31", mgmt.getRegularOvertimeWorkHour());
            setNumber(sheet, "B32", mgmt.getDailyMaleOvertimeMaleCnt());
            setNumber(sheet, "C32", mgmt.getDailyMaleOvertimeFemaleCnt());
            setNumber(sheet, "G32", mgmt.getDailyMaleOvertimeWorkHour());
            setNumber(sheet, "B33", mgmt.getDailyFemaleOvertimeMaleCnt());
            setNumber(sheet, "C33", mgmt.getDailyFemaleOvertimeFemaleCnt());
            setNumber(sheet, "G33", mgmt.getDailyFemaleOvertimeWorkHour());
            setNumber(sheet, "B34", mgmt.getMgmtNightMaleCnt());
            setNumber(sheet, "C34", mgmt.getMgmtNightFemaleCnt());
            setNumber(sheet, "G34", mgmt.getMgmtNightWorkHour());
            setNumber(sheet, "B35", mgmt.getRegularNightMaleCnt());
            setNumber(sheet, "C35", mgmt.getRegularNightFemaleCnt());
            setNumber(sheet, "G35", mgmt.getRegularNightWorkHour());
            setNumber(sheet, "B36", mgmt.getDailyMaleNightMaleCnt());
            setNumber(sheet, "C36", mgmt.getDailyMaleNightFemaleCnt());
            setNumber(sheet, "G36", mgmt.getDailyMaleNightWorkHour());
            setNumber(sheet, "B37", mgmt.getDailyFemaleNightMaleCnt());
            setNumber(sheet, "C37", mgmt.getDailyFemaleNightFemaleCnt());
            setNumber(sheet, "G37", mgmt.getDailyFemaleNightWorkHour());
            setNumber(sheet, "J26", mgmt.getMgmtDayHourlyCost());
            setNumber(sheet, "J27", mgmt.getRegularDayHourlyCost());
            setNumber(sheet, "J28", mgmt.getDailyMaleDayHourlyCost());
            setNumber(sheet, "J29", mgmt.getDailyFemaleDayHourlyCost());
            setNumber(sheet, "J36", mgmt.getDailyMaleNightHourlyCost());
            setNumber(sheet, "J37", mgmt.getDailyFemaleNightHourlyCost());

            // 5. 당일 경비 및 간접인건비
            setNumber(sheet, "E43", mgmt.getSuppliesDirectCost());
            setNumber(sheet, "E44", mgmt.getMealDirectCost());
            setNumber(sheet, "E45", mgmt.getEtcDirectCost());
            setNumber(sheet, "J46", mgmt.getAdminEtcIndirectCost());
            setNumber(sheet, "J47", mgmt.getDepreciationIndirectCost());
            setNumber(sheet, "J48", mgmt.getFinanceIndirectCost());
            setNumber(sheet, "J57", mgmt.getIndirectLaborCost());

            // =========================================================
            // 합계 및 계산 필드
            // DB 값이 아니라 Excel 수식으로 생성
            // =========================================================
            applyFormulas(sheet);

            workbook.setForceFormulaRecalculation(true);
            sheet.setForceFormulaRecalculation(true);

            // ExcelStyleUtil에 이미 있는 byte[] 변환 함수 사용
            return ExcelStyleUtil.toByteArray(workbook);

        } catch (Exception e) {
            throw new RuntimeException(
                    "일일 손익계산서 엑셀 생성 중 오류가 발생했습니다.",
                    e
            );
        } finally {
            if (workbook != null) {
                // ExcelStyleUtil의 Workbook별 스타일 캐시 정리
                ExcelStyleUtil.clearStyleCache(workbook);

                try {
                    workbook.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void applyFormulas(Sheet sheet) {
        // =========================================================
        // 원료 현재고
        // =========================================================
        setFormula(sheet, "L10", "B10+C10-J10");
        setFormula(sheet, "L11", "B11+C11-J11");

        // =========================================================
        // 부자재 현재고
        // =========================================================
        setFormula(
                sheet,
                "L15",
                "B15+C15-E15-G15-J15-K15"
        );

        setFormula(
                sheet,
                "L16",
                "B16+C16-E16-G16-J16-K16"
        );

        // =========================================================
        // 완제품 현재고
        // =========================================================
        setFormula(
                sheet,
                "L20",
                "B20+C20+E20-J20-K20"
        );

        setFormula(
                sheet,
                "L21",
                "B21+C21+E21-G21-J21-K21"
        );

        // =========================================================
        // 인건비 상세 계산
        // 기존 템플릿 행 구조 유지
        // =========================================================
        for (int row = 26; row <= 37; row++) {
            setFormula(
                    sheet,
                    "E" + row,
                    "B" + row + "+C" + row
            );

            setFormula(
                    sheet,
                    "K" + row,
                    "E" + row + "*G" + row + "*J" + row
            );
        }

        setFormula(sheet, "J30", "J26*1.5");
        setFormula(sheet, "J31", "J27*1.5");
        setFormula(sheet, "J32", "J28*1.5");
        setFormula(sheet, "J33", "J29*1.5");
        setFormula(sheet, "J34", "J26*1.5");
        setFormula(sheet, "J35", "J27*1.5");

        setFormula(sheet, "L26", "SUM(K26:K29)");
        setFormula(sheet, "L30", "SUM(K30:K33)");
        setFormula(sheet, "L34", "SUM(K34:K37)");
        setFormula(sheet, "L38", "SUM(L26:L37)");

        // =========================================================
        // 당일 경비 합계
        // =========================================================
        setFormula(sheet, "L43", "SUM(E43:K48)");

        // =========================================================
        // 생산금액 및 생산원가
        // =========================================================
        setFormula(sheet, "L52", "C21+E21");

        setFormula(sheet, "J53", "J11+K11");
        setFormula(sheet, "J54", "J16");
        setFormula(sheet, "J55", "SUM(J53:K54)");
        setFormula(sheet, "J56", "L38");
        setFormula(sheet, "J58", "SUM(J56:K57)");
        setFormula(sheet, "J59", "SUM(E43:I45)");
        setFormula(sheet, "J60", "SUM(J46:K48)");
        setFormula(sheet, "J61", "SUM(J59:K60)");
        setFormula(sheet, "J62", "I21");
        setFormula(sheet, "J63", "SUM(J62:K62)");
        setFormula(sheet, "J64", "J55+J58+J61+J63");

        setFormula(sheet, "L53", "J55+J58+J61+J63");

        // =========================================================
        // 경상이익 및 이익률
        // =========================================================
        setFormula(sheet, "L65", "L52-L53");
        setFormula(sheet, "L66", "IFERROR(L65/L52,0)");

        // =========================================================
        // 경상이익 숫자 표시 형식
        // 양수 : 22,222
        // 음수 : -22,222
        // 0    : -
        // =========================================================
        Cell profitCell = ExcelStyleUtil.getCellRef(sheet, "L65");

        CellStyle profitStyle = sheet.getWorkbook().createCellStyle();
        profitStyle.cloneStyleFrom(profitCell.getCellStyle());

        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();

        profitStyle.setDataFormat(
                dataFormat.getFormat("#,##0;-#,##0;\"-\"")
        );

        profitCell.setCellStyle(profitStyle);
    }

    private void clearTemplateValues(Sheet sheet) {

        String[] inventoryColumns = {
                "B", "C", "E", "G", "I", "J", "K"
        };

        int[] inventoryRows = {
                10, 11, 15, 16, 20, 21
        };

        for (int row : inventoryRows) {
            for (String column : inventoryColumns) {
                clearCell(
                        sheet,
                        column + row
                );
            }
        }

        // 인건비 입력영역의 기존 예시 값 제거
        for (int row = 26; row <= 37; row++) {
            clearCell(sheet, "B" + row);
            clearCell(sheet, "C" + row);
            clearCell(sheet, "G" + row);
            clearCell(sheet, "J" + row);
        }

        // 경비 입력영역
        clearCell(sheet, "E43");
        clearCell(sheet, "E44");
        clearCell(sheet, "E45");
        clearCell(sheet, "J46");
        clearCell(sheet, "J47");
        clearCell(sheet, "J48");

        // 생산원가 영역은 계산 수식이므로 삭제하지 않음.
        // J57은 위에서 입력값으로 다시 설정됨.

    }

    private void removeOtherSheets(Workbook workbook) {

        for (int index =
             workbook.getNumberOfSheets() - 1;
             index > 0;
             index--) {

            workbook.removeSheetAt(index);
        }
    }

    private void setDate(
            Sheet sheet,
            String cellRef,
            LocalDate value
    ) {
        Cell cell =
                ExcelStyleUtil.getCellRef(
                        sheet,
                        cellRef
                );

        cell.setCellValue(
                java.util.Date.from(
                        value
                                .atStartOfDay(
                                        ZoneId.systemDefault()
                                )
                                .toInstant()
                )
        );
    }

    private void setNumber(
            Sheet sheet,
            String cellRef,
            Number value
    ) {
        Cell cell =
                ExcelStyleUtil.getCellRef(
                        sheet,
                        cellRef
                );

        Row row = cell.getRow();

        // 기존 템플릿 셀 스타일은 그대로 유지
        ExcelStyleUtil.setNumberCell(
                row,
                cell.getColumnIndex(),
                value,
                cell.getCellStyle()
        );
    }

    private void setFormula(
            Sheet sheet,
            String cellRef,
            String formula
    ) {
        Cell cell =
                ExcelStyleUtil.getCellRef(
                        sheet,
                        cellRef
                );

        cell.setCellFormula(formula);
    }

    private void clearCell(
            Sheet sheet,
            String cellRef
    ) {
        Cell cell =
                ExcelStyleUtil.getCellRef(
                        sheet,
                        cellRef
                );

        // 값만 삭제하고 템플릿 스타일은 유지
        cell.setBlank();
    }
}
