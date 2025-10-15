package com.jct.mes_new.biz.lab.controller;

import com.jct.mes_new.biz.lab.service.RecipeService;
import com.jct.mes_new.biz.lab.vo.RecipeDetailVo;
import com.jct.mes_new.biz.lab.vo.RecipeRequestVo;
import com.jct.mes_new.biz.lab.vo.RecipeVo;
import com.jct.mes_new.config.util.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/getRecipeList")
    public List<RecipeVo> getRecipeList(@RequestBody RecipeVo recipeVo) {
        return recipeService.getRecipeList(recipeVo);
    }

    @GetMapping("/getRecipeInfo/{id}")
    public RecipeRequestVo getNewMaterialInfo (@PathVariable("id") String recipeId) {
        return recipeService.getNewMaterialInfo(recipeId);
    }

    @PostMapping("/saveRecipeInfo")
    public ResponseEntity<?> saveRecipeInfo(@RequestBody RecipeRequestVo request ) throws Exception {

        try {
            RecipeVo recipeInfo = request.getRecipeInfo();
            List<RecipeDetailVo> recipeList = request.getRecipeList();

            String result = recipeService.saveRecipeInfo(recipeInfo, recipeList);

            Map<String, String> response = Map.of("recipeId", result);

            return ResponseEntity.ok(ApiResponse.success(response));
            //return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("저장에 실패했습니다.", 400));
        }
    }

    @PostMapping("/downloadRecipe")
    public void downloadRecipe(@RequestBody RecipeRequestVo request, HttpServletResponse response) throws IOException {
        // 1. DB에서 데이터 조회
        RecipeVo recipeInfo = request.getRecipeInfo();
        List<RecipeDetailVo> recipeList = request.getRecipeList();
        // 2. 템플릿 Excel 불러오기 (resources/excel/lab/price_sheet_item_list.xlsx)
        ClassPathResource template = new ClassPathResource("excel/lab/price_sheet_item_list.xlsx");
        Workbook workbook = new XSSFWorkbook(template.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        // 3. 상단 제품 정보 매핑 (템플릿 row 번호는 예시)
        // 스타일
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);

        CellStyle boldBorderStyle = workbook.createCellStyle();
        boldBorderStyle.cloneStyleFrom(borderStyle);
        boldBorderStyle.setBorderTop(BorderStyle.MEDIUM);
        boldBorderStyle.setBorderBottom(BorderStyle.MEDIUM);
        boldBorderStyle.setBorderLeft(BorderStyle.MEDIUM);
        boldBorderStyle.setBorderRight(BorderStyle.MEDIUM);

        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldBorderStyle.setFont(boldFont);

        // 작성일 (E1)
        Row row1 = sheet.getRow(0); // 첫 번째 행
        if (row1 == null) row1 = sheet.createRow(0);
        Cell cellE1 = row1.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // E열
        cellE1.setCellValue("작성일 : " + recipeInfo.getRegDate());
        // 제품명 + 제품번호 (E2)
        Row row2 = sheet.getRow(1); // 두 번째 행
        if (row2 == null) row2 = sheet.createRow(1);
        Cell cellE2 = row2.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        // 1. 셀 스타일 생성 및 '자동 줄 바꿈' 활성화
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true); // ★★★ 핵심: 자동 줄 바꿈 설정
        wrapStyle.setAlignment(HorizontalAlignment.CENTER); // 수평 가운데 정렬
        wrapStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 수직 가운데 정렬
        // 2. 셀에 스타일 적용
        cellE2.setCellStyle(wrapStyle);
        cellE2.setCellValue(recipeInfo.getProdName()+"\n("+recipeInfo.getLabNo()+")");
        // 담당자 (E3)
        Row row3 = sheet.getRow(2); // 세 번째 행
        if (row3 == null) row3 = sheet.createRow(2);
        Cell cellE3 = row3.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cellE3.setCellValue(recipeInfo.getManagerName());
        // 영업담당자 / 거래처명 (E5)
        Row row4 = sheet.getRow(3); // 네 번째 행
        if (row4 == null) row4 = sheet.createRow(3);
        Cell cellE4 = row4.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cellE4.setCellValue("/ "+ recipeInfo.getClientName());
        // 4. 원료 데이터 매핑 (데이터 시작 row: 12번, 0부터 카운트)
        int startRow = 11;
        double totalContent = 0;
        double totalInPrice = 0;
        double totalUnitPrice = 0;

        for (int i = 0; i < recipeList.size(); i++) {
            RecipeDetailVo item = recipeList.get(i);
            Row row = sheet.getRow(startRow + i);
            if (row == null) row = sheet.createRow(startRow + i);

            double price = (item.getInPrice() == null) ? 0.0 : item.getInPrice().doubleValue();
            double unitPrice = (item.getUnitPrice() == null) ? 0.0 : item.getUnitPrice().doubleValue();

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(item.getItemName());
            cell1.setCellStyle(borderStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(item.getContent().doubleValue());
            cell2.setCellStyle(borderStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(price);
            cell3.setCellStyle(borderStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(unitPrice);
            cell4.setCellStyle(borderStyle);

            totalContent += item.getContent().doubleValue();
            totalInPrice += price;
            totalUnitPrice += unitPrice;
        }
        // 5. 합계 row 작성
        int totalRowIndex = startRow + recipeList.size();
        Row totalRow = sheet.getRow(totalRowIndex);
        if (totalRow == null) totalRow = sheet.createRow(totalRowIndex);
        Cell totalLabelCell = totalRow.createCell(1);
        totalLabelCell.setCellValue("Total");
        totalLabelCell.setCellStyle(boldBorderStyle);

        Cell totalContentCell = totalRow.createCell(2);
        totalContentCell.setCellValue(totalContent);
        totalContentCell.setCellStyle(boldBorderStyle);

        Cell totalInPriceCell = totalRow.createCell(3);
        totalInPriceCell.setCellValue(totalInPrice);
        totalInPriceCell.setCellStyle(boldBorderStyle);

        Cell totalUnitPriceCell = totalRow.createCell(4);
        totalUnitPriceCell.setCellValue(totalUnitPrice);
        totalUnitPriceCell.setCellStyle(boldBorderStyle);

        // 3. Response 설정
        String fileName = "recipe_" + recipeInfo.getProdName() + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.setHeader("Content-Transfer-Encoding", "binary");
        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
        workbook.close();
    }


}
