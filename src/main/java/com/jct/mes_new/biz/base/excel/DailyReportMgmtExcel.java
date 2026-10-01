package com.jct.mes_new.biz.base.excel;

import com.jct.mes_new.biz.base.mapper.DailyMgmtMapper;
import com.jct.mes_new.biz.base.vo.DailyMgmtVo;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

    private static final String TEMPLATE_PATH = "templates/daily_mgmt_template.xlsx";

    private static final DateTimeFormatter SHEET_NAME_FORMAT = DateTimeFormatter.ofPattern("MM.dd");

    private final DailyMgmtMapper dailyMgmtMapper;

    public byte[] download(DailyReportVo vo) {

        Workbook workbook = null;

        try {
            DailyMgmtVo mgmt =
                    dailyMgmtMapper.selectDailyReportInfo(
                            vo.getDailyId()
                    );

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

            // 템플릿에 들어 있던 예시 데이터 제거
            clearTemplateValues(sheet);
//
//            // =========================================================
//            // 1. 원료 당일 재고 현황
//            // =========================================================
//            setNumber(sheet, "B10", mgmt.getRawPrevStockQty());
//            setNumber(sheet, "B11", mgmt.getRawPrevStockAmt());
//
//            setNumber(sheet, "I10", mgmt.getRawOutsourceCostQty());
//            setNumber(sheet, "I11", mgmt.getRawOutsourceCostAmt());
//
//            // =========================================================
//            // 2. 부자재 당일 재고 현황
//            // =========================================================
//            setNumber(sheet, "B15", mgmt.getSubPrevStockQty());
//            setNumber(sheet, "B16", mgmt.getSubPrevStockAmt());
//
//            setNumber(sheet, "G15", mgmt.getSubDiscardQty());
//            setNumber(sheet, "G16", mgmt.getSubDiscardAmt());
//
//            setNumber(sheet, "J15", mgmt.getSubUsageQty());
//            setNumber(sheet, "J16", mgmt.getSubUsageAmt());
//
//            setNumber(sheet, "K15", mgmt.getSubOutsourceOutQty());
//            setNumber(sheet, "K16", mgmt.getSubOutsourceOutAmt());
//
//            // =========================================================
//            // 3. 완제품 당일 재고 현황
//            // =========================================================
//            setNumber(sheet, "B20", mgmt.getProdPrevStockQty());
//            setNumber(sheet, "B21", mgmt.getProdPrevStockAmt());
//
//            setNumber(sheet, "C20", mgmt.getProdQty());
//            setNumber(sheet, "C21", mgmt.getProdAmt());
//
//            setNumber(sheet, "E20", mgmt.getProdOutsourceQty());
//            setNumber(sheet, "E21", mgmt.getProdOutsourceAmt());
//
//            setNumber(sheet, "G20", mgmt.getProdDiscardQty());
//            setNumber(sheet, "G21", mgmt.getProdDiscardAmt());
//
//            setNumber(sheet, "I20", mgmt.getProdOutsourceCostQty());
//            setNumber(sheet, "I21", mgmt.getProdOutsourceCostAmt());
//
//            setNumber(sheet, "J20", mgmt.getProdShipmentQty());
//            setNumber(sheet, "J21", mgmt.getProdShipmentAmt());
//
//            setNumber(sheet, "K20", mgmt.getProdReturnQty());
//            setNumber(sheet, "K21", mgmt.getProdReturnAmt());
//
//            // =========================================================
//            // 4. 당일 경비 현황
//            // 직접비용
//            // =========================================================
//            setNumber(sheet, "E43", mgmt.getConsumablesAmt());
//            setNumber(sheet, "E44", mgmt.getMealAmt());
//            setNumber(sheet, "E45", mgmt.getOtherDirectCostAmt());
//
//            // =========================================================
//            // 간접비용
//            // =========================================================
//            setNumber(sheet, "J46", mgmt.getOtherSgaCostAmt());
//            setNumber(sheet, "J47", mgmt.getDepreciationAmt());
//            setNumber(sheet, "J48", mgmt.getFinanceCostAmt());
//
//            // =========================================================
//            // 5. 생산원가 입력값
//            // =========================================================
//            setNumber(sheet, "J53", mgmt.getRawMaterialCostAmt());
//            setNumber(sheet, "J54", mgmt.getSubMaterialCostAmt());
//
//            setNumber(sheet, "J56", mgmt.getDirectLaborCostAmt());
//            setNumber(sheet, "J57", mgmt.getIndirectLaborCostAmt());
//
//            setNumber(sheet, "J59", mgmt.getManufacturingCostAmt());
//            setNumber(sheet, "J60", mgmt.getSgaCostAmt());
//
//            // 외주 생산 비용
//            setNumber(sheet, "J62", mgmt.getProdOutsourceCostAmt());

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
                "B20+C20+E20-G20-J20-K20"
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

        setFormula(sheet, "J55", "SUM(J53:K54)");
        setFormula(sheet, "J58", "SUM(J56:K57)");
        setFormula(sheet, "J61", "SUM(J59:K60)");
        setFormula(sheet, "J63", "SUM(J62:K62)");
        setFormula(sheet, "J64", "J55+J58+J61+J63");

        setFormula(sheet, "L53", "J55+J58+J61+J63");
        setFormula(sheet, "L65", "L52-L53");
        setFormula(sheet, "L66", "IFERROR(L65/L52,0)");
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

        // 생산원가 입력영역
        clearCell(sheet, "J53");
        clearCell(sheet, "J54");
        clearCell(sheet, "J56");
        clearCell(sheet, "J57");
        clearCell(sheet, "J59");
        clearCell(sheet, "J60");
        clearCell(sheet, "J62");
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
