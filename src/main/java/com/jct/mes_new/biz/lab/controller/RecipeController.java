package com.jct.mes_new.biz.lab.controller;

import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.lab.service.RecipeService;
import com.jct.mes_new.biz.lab.vo.AllIngredientVo;
import com.jct.mes_new.biz.lab.vo.RecipeDetailVo;
import com.jct.mes_new.biz.lab.vo.RecipeRequestVo;
import com.jct.mes_new.biz.lab.vo.RecipeVo;
import com.jct.mes_new.config.common.MessageUtil;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/recipe")
public class RecipeController {

    private final RecipeService recipeService;
    private final MessageUtil messageUtil;


    @GetMapping("/getProdTypeList")
    public List<RecipeVo> getProdTypeList(){
        return recipeService.getProdTypeList();
    }

    @PostMapping("/getRecipeList")
    public List<RecipeVo> getRecipeList(@RequestBody RecipeVo recipeVo) {
        return recipeService.getRecipeList(recipeVo);
    }

    @GetMapping("/getRecipeInfo/{id}")
    public RecipeRequestVo getNewMaterialInfo (@PathVariable("id") String recipeId) {
        return recipeService.getNewMaterialInfo(recipeId);
    }

    @PostMapping("/saveRecipeInfo")
    public ResponseEntity<ApiResponse<Map<String, String>>> saveRecipeInfo(@RequestBody RecipeRequestVo request ) throws Exception {
            RecipeVo recipeInfo = request.getRecipeInfo();
            List<RecipeDetailVo> recipeList = request.getRecipeList();

            String recipeId = recipeService.saveRecipeInfo(recipeInfo, recipeList);

            return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), Map.of("recipeId", recipeId)));
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

        // 1️⃣ 폰트 설정
        Font font = workbook.createFont();
        font.setFontName("맑은 고딕");     // 폰트명
        font.setFontHeightInPoints((short) 10);  // 글자 크기

        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setFont(font);

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

        DecimalFormat df = new DecimalFormat("###,###");

        for (int i = 0; i < recipeList.size(); i++) {
            RecipeDetailVo item = recipeList.get(i);
            Row row = sheet.getRow(startRow + i);
            if (row == null) row = sheet.createRow(startRow + i);

            double price = (item.getInPrice() == null) ? 0.0 : item.getInPrice().doubleValue();
            double unitPrice = (item.getUnitPrice() == null) ? 0.0 : item.getUnitPrice().doubleValue();

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(item.getItemName());
            cell1.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "LEFT" ));

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(item.getContent().doubleValue());
            cell2.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT" ));

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(df.format(price));
            cell3.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT" ));

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(unitPrice);
            cell4.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT" ));

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
        totalLabelCell.setCellStyle(ExcelStyleUtil.getBoldBorderStyle(workbook, "CENTER" ));

        Cell totalContentCell = totalRow.createCell(2);
        totalContentCell.setCellValue(df.format(totalContent));
        totalContentCell.setCellStyle(ExcelStyleUtil.getBoldBorderStyle(workbook, "RIGHT" ));

        Cell totalInPriceCell = totalRow.createCell(3);
        totalInPriceCell.setCellValue(df.format(totalInPrice));
        totalInPriceCell.setCellStyle(ExcelStyleUtil.getBoldBorderStyle(workbook, "RIGHT" ));

        Cell totalUnitPriceCell = totalRow.createCell(4);
        totalUnitPriceCell.setCellValue(totalUnitPrice);
        totalUnitPriceCell.setCellStyle(ExcelStyleUtil.getBoldBorderStyle(workbook, "RIGHT" ));

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

    @GetMapping("/downloadIngredient/{id}")
    public void downloadIngredient(@PathVariable("id") String recipeId, HttpServletResponse response) throws IOException {
        // 1. DB에서 데이터 조회
        Map<String, Object> map = recipeService.getProdInfo(recipeId);
        List<AllIngredientVo> allIngredientInList = recipeService.allIngredientInList(recipeId);
        List<AllIngredientVo> allIngredientOutList = recipeService.allIngredientOutList(recipeId);

        LocalDate toDay = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM. dd. yyyy", Locale.US);
        String formattedDate = toDay.format(formatter);

        // 2. 템플릿 Excel 불러오기 (resources/excel/lab/price_sheet_item_list.xlsx)
        ClassPathResource template = new ClassPathResource("excel/lab/all_Ingredient_list.xlsx");
        Workbook workbook = new XSSFWorkbook(template.getInputStream());
        Sheet sheet  = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        Sheet sheet2 = workbook.getSheetAt(2);
        Sheet sheet3 = workbook.getSheetAt(3);

        CellStyle mergedCellStyle = workbook.createCellStyle();
        mergedCellStyle.setAlignment(HorizontalAlignment.CENTER); // 가로 가운데 정렬
        mergedCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 세로 가운데 정렬
        mergedCellStyle.setBorderBottom(BorderStyle.THIN); // 아래쪽 테두리
        mergedCellStyle.setBorderTop(BorderStyle.THIN); // 위쪽 테두리
        mergedCellStyle.setBorderLeft(BorderStyle.THIN); // 왼쪽 테두리
        mergedCellStyle.setBorderRight(BorderStyle.THIN); // 오른쪽 테두리

        //sheet 제품명 , 날짜
        Row prodRow = sheet.getRow(1); // 두 번째 행
        if (prodRow == null) prodRow = sheet.createRow(1);
        Cell cellE1 = prodRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); //
        cellE1.setCellValue("Product Name : " + map.get("prodName"));

        Row dateRow = sheet.getRow(1);
        if (dateRow == null) dateRow = sheet.createRow(6);
        Cell dateCell = dateRow.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        dateCell.setCellValue(toDay);

        int startRow = 3;
        double totalContent = 0;

        String krIngredientNames =  allIngredientInList.stream()
                .map(AllIngredientVo::getKrIngredientName)
                .filter(Objects::nonNull)
                .filter(name -> !name.isBlank())
                .collect(Collectors.joining(", "));
        String enIngredientNames =  allIngredientInList.stream()
                .map(AllIngredientVo::getEnIngredientName)
                .filter(Objects::nonNull)
                .filter(name -> !name.isBlank())
                .collect(Collectors.joining(", "));

        for (int i = 0; i < allIngredientInList.size(); i++) {
            AllIngredientVo item = allIngredientInList.get(i);
            Row row = sheet.getRow(startRow + i);
            if (row == null) row = sheet.createRow(startRow + i);

            double content = (item.getContent() == null) ? 0.0 : item.getContent().doubleValue();

            Cell cell0 = row.getCell(0);
            cell0.setCellValue(item.getRowNum().intValue());

            Cell cell1 = row.getCell(1);
            cell1.setCellValue(item.getKrIngredientName());

            Cell cell2 = row.getCell(2);
            cell2.setCellValue(item.getEnIngredientName());

            Cell cell3 = row.getCell(3);
            cell3.setCellValue(content);

            Cell cell4 = row.getCell(4);
            cell4.setCellValue("ICID");

            Cell cell5 = row.getCell(5);
            cell5.setCellValue(item.getCasNo());

            Cell cell6 = row.getCell(6);
            cell6.setCellValue(item.getFunctionName());

            totalContent += content;
        }
        // 5. 합계 row 작성
        int sumRowNum = startRow + allIngredientInList.size();
        int totalRowNum = sumRowNum + 1;

        CellRangeAddress sumMerge = new CellRangeAddress(sumRowNum, sumRowNum, 0, 2);
        CellRangeAddress totalMerge = new CellRangeAddress(totalRowNum, totalRowNum, 0, 6);

        // Sum
        Row sumRow = sheet.createRow(sumRowNum);
        Cell sumLabel = sumRow.createCell(0);
        sumLabel.setCellValue("Sum");
        sumLabel.setCellStyle(mergedCellStyle);

        Cell sumValue = sumRow.createCell(3);
        sumValue.setCellValue(totalContent);
        sumValue.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT"));

        // Total items
        Row totalRow = sheet.createRow(totalRowNum);
        Cell totalLabel = totalRow.createCell(0);
        totalLabel.setCellValue("Total " + allIngredientInList.size() + " items");
        totalLabel.setCellStyle(mergedCellStyle);

        sheet.addMergedRegion(sumMerge);
        RegionUtil.setBorderTop(BorderStyle.THIN, sumMerge, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, sumMerge, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, sumMerge, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, sumMerge, sheet);
        sheet.addMergedRegion(totalMerge);
        RegionUtil.setBorderTop(BorderStyle.THIN, totalMerge, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, totalMerge, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, totalMerge, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, totalMerge, sheet);


        /* 두번째 시트*/
        //sheet1 제품명 , 날짜
        Row prodRow1 = sheet1.getRow(1); // 두 번째 행
        if (prodRow1 == null) prodRow1 = sheet1.createRow(1);
        Cell cellA1 = prodRow1.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); //
        cellA1.setCellValue("Product Name : " + map.get("prodName"));

        Row dateRow1 = sheet1.getRow(1);
        if (dateRow1 == null) dateRow1 = sheet1.createRow(9);
        Cell dateCell1 = dateRow1.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        dateCell1.setCellValue(formattedDate);

        int startRow1 = 3;
        double totalContent1 = 0;
        double totalComposition = 0;
        double totalInFormula = 0;

        for (int i = 0; i < allIngredientOutList.size(); i++) {
            AllIngredientVo item = allIngredientOutList.get(i);
            Row row = sheet1.getRow(startRow1 + i);
            if (row == null) row = sheet1.createRow(startRow1 + i);

            double content = (item.getContent() == null) ? 0.0 : item.getContent().doubleValue();
            double composition = (item.getComposition() == null) ? 0.0 : item.getComposition().doubleValue();
            double inFormula = (item.getInFormula() == null) ? 0.0 : item.getInFormula().doubleValue();

            Cell cell0 = row.getCell(0);
            cell0.setCellValue(item.getRowNum().intValue());

            Cell cell1 = row.getCell(1);
            cell1.setCellValue(item.getItemName());

            Cell cell2 = row.getCell(2);
            cell2.setCellValue(item.getKrIngredientName());

            Cell cell3 = row.getCell(3);
            cell3.setCellValue(item.getEnIngredientName());

            Cell cell4 = row.getCell(4);
            cell4.setCellValue(content);

            Cell cell5 = row.getCell(5);
            cell5.setCellValue(composition);

            Cell cell6 = row.getCell(6);
            cell6.setCellValue(inFormula);

            Cell cell7 = row.getCell(7);
            cell7.setCellValue("ICID");

            Cell cell8 = row.getCell(8);
            cell8.setCellValue(item.getCasNo());

            Cell cell9 = row.getCell(9);
            cell9.setCellValue(item.getFunctionName());

            totalContent1 += content;
            totalComposition += item.getComposition().doubleValue();
            totalInFormula += item.getInFormula().doubleValue();
        }

        int sumRowNum1 = startRow1 + allIngredientOutList.size();
        int totalRowNum1 = sumRowNum1 + 1;

        CellRangeAddress sumMerge1 = new CellRangeAddress(sumRowNum1, sumRowNum1, 0, 3);
        CellRangeAddress totalMerge1 = new CellRangeAddress(totalRowNum1, totalRowNum1, 0, 9);

        // Sum
        Row sumRow1 = sheet1.createRow(sumRowNum1);
        Cell sumLabel1 = sumRow1.createCell(0);
        sumLabel1.setCellValue("Sum");
        sumLabel1.setCellStyle(mergedCellStyle);

        Cell sumValue1 = sumRow1.createCell(4);
        sumValue1.setCellValue(totalContent);
        sumValue1.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT"));

        Cell sumValue2 = sumRow1.createCell(5);
        sumValue2.setCellValue(totalComposition);
        sumValue2.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT"));

        Cell sumValue3 = sumRow1.createCell(6);
        sumValue3.setCellValue(totalInFormula);
        sumValue3.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT"));

        // Total items
        Row totalRow1 = sheet1.createRow(totalRowNum1);
        Cell totalLabel1 = totalRow1.createCell(0);
        totalLabel1.setCellValue("Total " + allIngredientOutList.size() + " items");
        totalLabel1.setCellStyle(mergedCellStyle);

        sheet1.addMergedRegion(sumMerge1);
        RegionUtil.setBorderTop(BorderStyle.THIN, sumMerge1, sheet1);
        RegionUtil.setBorderBottom(BorderStyle.THIN, sumMerge1, sheet1);
        RegionUtil.setBorderLeft(BorderStyle.THIN, sumMerge1, sheet1);
        RegionUtil.setBorderRight(BorderStyle.THIN, sumMerge1, sheet1);

        sheet1.addMergedRegion(totalMerge1);
        RegionUtil.setBorderTop(BorderStyle.THIN, totalMerge1, sheet1);
        RegionUtil.setBorderBottom(BorderStyle.THIN, totalMerge1, sheet1);
        RegionUtil.setBorderLeft(BorderStyle.THIN, totalMerge1, sheet1);
        RegionUtil.setBorderRight(BorderStyle.THIN, totalMerge1, sheet1);


        //세번째 시트
        Row prodTypeRow2 = sheet2.getRow(2); //
        if (prodTypeRow2 == null) prodTypeRow2 = sheet2.createRow(2);
        Cell cellB = prodTypeRow2.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); //
        cellB.setCellValue( map.get("prodTypeName").toString() );

        Row dateRow2 = sheet2.getRow(2);
        if (dateRow2 == null) dateRow2 = sheet2.createRow(2);
        Cell dateCell2 = dateRow2.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        dateCell2.setCellValue(toDay);

        Row prodRow2 = sheet2.getRow(4);
        if (prodRow2 == null) prodRow2 = sheet2.createRow(4);
        Cell prodCell2 = prodRow2.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        prodCell2.setCellValue(map.get("prodName").toString());
        Map<String, Object> map1 = recipeService.getProdTypeInfo(map.get("prodTypeName").toString());

        if ( map1 != null ) {
            //사용법
            Row useRow = sheet2.getRow(8);
            if (useRow == null) useRow = sheet2.createRow(8);
            Cell useCell = useRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            useCell.setCellValue( map1.get("krUse").toString());

            Row cautionRow = sheet2.getRow(24);
            if (cautionRow == null) cautionRow = sheet2.createRow(24);
            Cell cautionCell = cautionRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cautionCell.setCellValue( map1.get("krCaution").toString());
            //기능성
            Row craftinessRow = sheet2.getRow(32);
            if (craftinessRow == null) craftinessRow = sheet2.createRow(32);
            Cell craftinessnCell = craftinessRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            craftinessnCell.setCellValue( map1.get("krCraftiness").toString());
            //용법용량
            Row menualRow = sheet2.getRow(34);
            if (menualRow == null) menualRow = sheet2.createRow(34);
            Cell menualCell = menualRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            menualCell.setCellValue( map1.get("krUsage").toString());
            //효능효과
            Row effcacyRow = sheet2.getRow(36);
            if (effcacyRow == null) effcacyRow = sheet2.createRow(36);
            Cell effcacyCell = effcacyRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            effcacyCell.setCellValue( map1.get("krEffect").toString());
        }

        Row koAllRow2 = sheet2.getRow(10);
        if (koAllRow2 == null) koAllRow2 = sheet2.createRow(10);
        Cell koAllCell2 = koAllRow2.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        koAllCell2.setCellValue( krIngredientNames );

        Row enAllRow2 = sheet2.getRow(15);
        if (enAllRow2 == null) enAllRow2 = sheet2.createRow(15);
        Cell enAllCell2 = enAllRow2.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        enAllCell2.setCellValue( enIngredientNames );

        //네번째 시트
        Row prodTypeRow3 = sheet3.getRow(2); //
        if (prodTypeRow3 == null) prodTypeRow3 = sheet3.createRow(2);
        Cell cellB1 = prodTypeRow3.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); //
        cellB1.setCellValue( map.get("enProdTypeName").toString() );

        Row dateRow3 = sheet3.getRow(2);
        if (dateRow3 == null) dateRow3 = sheet3.createRow(2);
        Cell dateCell3 = dateRow3.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        dateCell3.setCellValue(formattedDate);

        Row prodRow3 = sheet3.getRow(4);
        if (prodRow3 == null) prodRow3 = sheet3.createRow(4);
        Cell prodCell3 = prodRow3.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        prodCell3.setCellValue(map.get("prodName").toString());

        if ( map1 != null ) {
            Row useRow = sheet3.getRow(8);
            if (useRow == null) useRow = sheet3.createRow(8);
            Cell useCell = useRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            useCell.setCellValue( map1.get("enUse").toString());

            Row cautionRow = sheet3.getRow(24);
            if (cautionRow == null) cautionRow = sheet3.createRow(24);
            Cell cautionCell = cautionRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cautionCell.setCellValue( map1.get("enCaution").toString());
            //기능성
            Row craftinessRow = sheet3.getRow(32);
            if (craftinessRow == null) craftinessRow = sheet3.createRow(32);
            Cell craftinessnCell = craftinessRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            craftinessnCell.setCellValue( map1.get("enCraftiness").toString());
            //용법용량
            Row menualRow = sheet3.getRow(34);
            if (menualRow == null) menualRow = sheet3.createRow(34);
            Cell menualCell = menualRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            menualCell.setCellValue( map1.get("enUsage").toString());
            //효능효과
            Row effcacyRow = sheet3.getRow(36);
            if (effcacyRow == null) effcacyRow = sheet3.createRow(36);
            Cell effcacyCell = effcacyRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            effcacyCell.setCellValue( map1.get("enEffect").toString());
        }

        Row koAllRow1 = sheet3.getRow(10);
        if (koAllRow1 == null) koAllRow1 = sheet3.createRow(10);
        Cell koAllCell1 = koAllRow1.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        koAllCell1.setCellValue( krIngredientNames );

        Row enAllRow1 = sheet3.getRow(15);
        if (enAllRow1 == null) enAllRow1 = sheet3.createRow(5);
        Cell enAllCell = enAllRow1.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        enAllCell.setCellValue( enIngredientNames );

        // 3. Response 설정
        String fileName = "recipe_" + map.get("prodName") + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.setHeader("Content-Transfer-Encoding", "binary");
        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
        workbook.close();

    }

    @GetMapping("/downloadIngredientCn/{id}")
    public void downloadIngredientCn(@PathVariable("id") String recipeId, HttpServletResponse response) throws IOException {
        // 1. DB에서 데이터 조회
        Map<String, Object> map = recipeService.getProdInfo(recipeId);
        List<AllIngredientVo> allIngredientInList = recipeService.allIngredientInList(recipeId);
        List<AllIngredientVo> allIngredientOutList = recipeService.allIngredientOutList(recipeId);

        LocalDate toDay = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM. dd. yyyy", Locale.US);
        String formattedDate = toDay.format(formatter);

        // 2. 템플릿 Excel 불러오기 (resources/excel/lab/price_sheet_item_list.xlsx)
        ClassPathResource template = new ClassPathResource("excel/lab/all_Ingredient_list_cn.xlsx");
        Workbook workbook = new XSSFWorkbook(template.getInputStream());
        Sheet sheet  = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        Sheet sheet2 = workbook.getSheetAt(2);
        Sheet sheet3 = workbook.getSheetAt(3);

        CellStyle mergedCellStyle = workbook.createCellStyle();
        mergedCellStyle.setAlignment(HorizontalAlignment.CENTER); // 가로 가운데 정렬
        mergedCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 세로 가운데 정렬
        mergedCellStyle.setBorderBottom(BorderStyle.THIN); // 아래쪽 테두리
        mergedCellStyle.setBorderTop(BorderStyle.THIN); // 위쪽 테두리
        mergedCellStyle.setBorderLeft(BorderStyle.THIN); // 왼쪽 테두리
        mergedCellStyle.setBorderRight(BorderStyle.THIN); // 오른쪽 테두리

        // 정렬을 위한 셀 스타일 (가운데 정렬)
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //sheet 제품명 , 날짜
        Row prodRow = sheet.getRow(1); // 두 번째 행
        if (prodRow == null) prodRow = sheet.createRow(1);
        Cell cellE1 = prodRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); //
        cellE1.setCellValue("Product Name : " + map.get("prodName"));

        Row dateRow = sheet.getRow(1);
        if (dateRow == null) dateRow = sheet.createRow(7);
        Cell dateCell = dateRow.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        dateCell.setCellValue(toDay);

        int startRow = 3;
        double totalContent = 0;

        String krIngredientNames =  allIngredientInList.stream()
                .map(AllIngredientVo::getKrIngredientName)
                .filter(Objects::nonNull)
                .filter(name -> !name.isBlank())
                .collect(Collectors.joining(", "));
        String enIngredientNames =  allIngredientInList.stream()
                .map(AllIngredientVo::getEnIngredientName)
                .filter(Objects::nonNull)
                .filter(name -> !name.isBlank())
                .collect(Collectors.joining(", "));

        for (int i = 0; i < allIngredientInList.size(); i++) {
            AllIngredientVo item = allIngredientInList.get(i);
            Row row = sheet.getRow(startRow + i);
            if (row == null) row = sheet.createRow(startRow + i);

            double content = (item.getContent() == null) ? 0.0 : item.getContent().doubleValue();

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(item.getRowNum().intValue());
            cell0.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "CENTER"));

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(item.getKrIngredientName());
            cell1.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "LEFT"));

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(item.getEnIngredientName());
            cell2.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "LEFT"));

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(item.getCnIngredientName());
            cell3.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "LEFT"));

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(content);
            cell4.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT"));

            Cell cell5 = row.createCell(5);
            cell5.setCellValue("ICID");
            cell5.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "CENTER"));

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(item.getCasNo());
            cell6.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "CENTER"));

            Cell cell7 = row.createCell(7);
            cell7.setCellValue(item.getFunctionName());
            cell7.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "LEFT"));

            totalContent += content;
        }

        // 5. 합계 row 작성
        int sumRowNum = startRow + allIngredientInList.size();
        int totalRowNum = sumRowNum + 1;

        CellRangeAddress sumMerge = new CellRangeAddress(sumRowNum, sumRowNum, 0, 3);
        CellRangeAddress totalMerge = new CellRangeAddress(totalRowNum, totalRowNum, 0, 7);

        // Sum
        Row sumRow = sheet.createRow(sumRowNum);
        Cell sumLabel = sumRow.createCell(0);
        sumLabel.setCellValue("Sum");
        sumLabel.setCellStyle(mergedCellStyle);

        Cell sumValue = sumRow.createCell(4);
        sumValue.setCellValue(totalContent);
        sumValue.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT"));

        // Total items
        Row totalRow = sheet.createRow(totalRowNum);
        Cell totalLabel = totalRow.createCell(0);
        totalLabel.setCellValue("Total " + allIngredientInList.size() + " items");
        totalLabel.setCellStyle(mergedCellStyle);

        sheet.addMergedRegion(sumMerge);
        RegionUtil.setBorderTop(BorderStyle.THIN, sumMerge, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, sumMerge, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, sumMerge, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, sumMerge, sheet);
        sheet.addMergedRegion(totalMerge);
        RegionUtil.setBorderTop(BorderStyle.THIN, totalMerge, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, totalMerge, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, totalMerge, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, totalMerge, sheet);


        /* 두번째 시트*/
        //sheet1 제품명 , 날짜
        Row prodRow1 = sheet1.getRow(1); // 두 번째 행
        if (prodRow1 == null) prodRow1 = sheet1.createRow(1);
        Cell cellA1 = prodRow1.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); //
        cellA1.setCellValue("Product Name : " + map.get("prodName"));

        Row dateRow1 = sheet1.getRow(1);
        if (dateRow1 == null) dateRow1 = sheet1.createRow(9);
        Cell dateCell1 = dateRow1.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        dateCell1.setCellValue(formattedDate);

        int startRow1 = 3;
        double totalContent1 = 0;
        double totalComposition = 0;
        double totalInFormula = 0;

        for (int i = 0; i < allIngredientOutList.size(); i++) {
            AllIngredientVo item = allIngredientOutList.get(i);
            Row row = sheet1.getRow(startRow1 + i);
            if (row == null) row = sheet1.createRow(startRow1 + i);
            double content = (item.getContent() == null) ? 0.0 : item.getContent().doubleValue();
            double composition = (item.getComposition() == null) ? 0.0 : item.getComposition().doubleValue();
            double inFormula = (item.getInFormula() == null) ? 0.0 : item.getInFormula().doubleValue();

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(item.getRowNum().intValue());
            cell0.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "CENTER"));

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(item.getKrIngredientName());
            cell1.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "LEFT"));

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(item.getEnIngredientName());
            cell2.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "LEFT"));

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(item.getCnIngredientName());
            cell3.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "LEFT"));

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(content);
            cell4.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT"));

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(composition);
            cell5.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT"));

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(inFormula);
            cell6.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "RIGHT"));

            Cell cell7 = row.createCell(7);
            cell7.setCellValue("ICID");
            cell7.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "CENTER"));

            Cell cell8 = row.createCell(8);
            cell8.setCellValue(item.getCasNo());
            cell8.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "CENTER"));

            Cell cell9 = row.createCell(9);
            cell9.setCellValue(item.getFunctionName());
            cell9.setCellStyle(ExcelStyleUtil.getBorderStyle(workbook, "LEFT"));

            totalContent1 += content;
            totalComposition += item.getComposition().doubleValue();
            totalInFormula += item.getInFormula().doubleValue();
        }

        int sumRowNum1 = startRow1 + allIngredientOutList.size();
        int totalRowNum1 = sumRowNum1 + 1;

        CellRangeAddress sumMerge1 = new CellRangeAddress(sumRowNum1, sumRowNum1, 0, 4);
        CellRangeAddress totalMerge1 = new CellRangeAddress(totalRowNum1, totalRowNum1, 0, 9);

        // Sum
        Row sumRow1 = sheet1.createRow(sumRowNum1);
        Cell sumLabel1 = sumRow1.createCell(0);
        sumLabel1.setCellValue("Sum");
        sumLabel1.setCellStyle(mergedCellStyle);

        Cell sumValue1 = sumRow1.createCell(5);
        sumValue1.setCellValue(totalContent);
        sumLabel1.setCellStyle(mergedCellStyle);

        Cell sumValue2 = sumRow1.createCell(6);
        sumValue2.setCellValue(totalComposition);

        Cell sumValue3 = sumRow1.createCell(7);
        sumValue3.setCellValue(totalInFormula);
        sumValue3.setCellStyle(mergedCellStyle);

        // Total items
        Row totalRow1 = sheet1.createRow(totalRowNum1);
        Cell totalLabel1 = totalRow1.createCell(0);
        totalLabel1.setCellValue("Total " + allIngredientOutList.size() + " items");
        totalLabel1.setCellStyle(mergedCellStyle);

        sheet1.addMergedRegion(sumMerge1);
        RegionUtil.setBorderTop(BorderStyle.THIN, sumMerge1, sheet1);
        RegionUtil.setBorderBottom(BorderStyle.THIN, sumMerge1, sheet1);
        RegionUtil.setBorderLeft(BorderStyle.THIN, sumMerge1, sheet1);
        RegionUtil.setBorderRight(BorderStyle.THIN, sumMerge1, sheet1);
        sheet1.addMergedRegion(totalMerge1);
        RegionUtil.setBorderTop(BorderStyle.THIN, totalMerge1, sheet1);
        RegionUtil.setBorderBottom(BorderStyle.THIN, totalMerge1, sheet1);
        RegionUtil.setBorderLeft(BorderStyle.THIN, totalMerge1, sheet1);
        RegionUtil.setBorderRight(BorderStyle.THIN, totalMerge1, sheet1);


        //세번째 시트
        Row prodTypeRow2 = sheet2.getRow(2); //
        if (prodTypeRow2 == null) prodTypeRow2 = sheet2.createRow(2);
        Cell cellB = prodTypeRow2.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); //
        cellB.setCellValue( map.get("prodTypeName").toString() );

        Row dateRow2 = sheet2.getRow(2);
        if (dateRow2 == null) dateRow2 = sheet2.createRow(2);
        Cell dateCell2 = dateRow2.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        dateCell2.setCellValue(toDay);

        Row prodRow2 = sheet2.getRow(4);
        if (prodRow2 == null) prodRow2 = sheet2.createRow(4);
        Cell prodCell2 = prodRow2.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        prodCell2.setCellValue(map.get("prodName").toString());
        Map<String, Object> map1 = recipeService.getProdTypeInfo(map.get("prodTypeName").toString());

        if ( map1 != null ) {
            Row useRow = sheet2.getRow(8);
            if (useRow == null) useRow = sheet2.createRow(8);
            Cell useCell = useRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            useCell.setCellValue( map1.get("krUse").toString());

            Row cautionRow = sheet2.getRow(24);
            if (cautionRow == null) cautionRow = sheet2.createRow(24);
            Cell cautionCell = cautionRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cautionCell.setCellValue( map1.get("krCaution").toString());
            //기능성
            Row craftinessRow = sheet2.getRow(32);
            if (craftinessRow == null) craftinessRow = sheet2.createRow(32);
            Cell craftinessnCell = craftinessRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            craftinessnCell.setCellValue( map1.get("krCraftiness").toString());
            //용법용량
            Row menualRow = sheet2.getRow(34);
            if (menualRow == null) menualRow = sheet2.createRow(34);
            Cell menualCell = menualRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            menualCell.setCellValue( map1.get("krUsage").toString());
            //효능효과
            Row effcacyRow = sheet2.getRow(36);
            if (effcacyRow == null) effcacyRow = sheet2.createRow(36);
            Cell effcacyCell = effcacyRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            effcacyCell.setCellValue( map1.get("krEffect").toString());
        }
        Row koAllRow2 = sheet2.getRow(10);
        if (koAllRow2 == null) koAllRow2 = sheet2.createRow(10);
        Cell koAllCell2 = koAllRow2.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        koAllCell2.setCellValue( krIngredientNames );

        Row enAllRow2 = sheet2.getRow(15);
        if (enAllRow2 == null) enAllRow2 = sheet2.createRow(5);
        Cell enAllCell2 = enAllRow2.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        enAllCell2.setCellValue( enIngredientNames );
        //네번째 시트
        Row prodTypeRow3 = sheet3.getRow(2); //
        if (prodTypeRow3 == null) prodTypeRow3 = sheet3.createRow(2);
        Cell cellB1 = prodTypeRow3.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); //
        cellB1.setCellValue( map.get("enProdTypeName").toString() );

        Row dateRow3 = sheet3.getRow(2);
        if (dateRow3 == null) dateRow3 = sheet3.createRow(2);
        Cell dateCell3 = dateRow3.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        dateCell3.setCellValue(formattedDate);
        Row prodRow3 = sheet3.getRow(4);
        if (prodRow3 == null) prodRow3 = sheet3.createRow(4);
        Cell prodCell3 = prodRow3.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        prodCell3.setCellValue(map.get("prodName").toString());

        if ( map1 != null ) {
            Row useRow = sheet3.getRow(8);
            if (useRow == null) useRow = sheet3.createRow(8);
            Cell useCell = useRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            useCell.setCellValue( map1.get("enUse").toString());

            Row cautionRow = sheet3.getRow(24);
            if (cautionRow == null) cautionRow = sheet3.createRow(24);
            Cell cautionCell = cautionRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cautionCell.setCellValue( map1.get("enCaution").toString());
            //기능성
            Row craftinessRow = sheet3.getRow(32);
            if (craftinessRow == null) craftinessRow = sheet3.createRow(32);
            Cell craftinessnCell = craftinessRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            craftinessnCell.setCellValue( map1.get("enCraftiness").toString());
            //용법용량
            Row menualRow = sheet3.getRow(34);
            if (menualRow == null) menualRow = sheet3.createRow(34);
            Cell menualCell = menualRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            menualCell.setCellValue( map1.get("enUsage").toString());
            //효능효과
            Row effcacyRow = sheet3.getRow(36);
            if (effcacyRow == null) effcacyRow = sheet3.createRow(36);
            Cell effcacyCell = effcacyRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            effcacyCell.setCellValue( map1.get("enEffect").toString());
        }
        Row koAllRow1 = sheet3.getRow(10);
        if (koAllRow1 == null) koAllRow1 = sheet3.createRow(10);
        Cell koAllCell1 = koAllRow1.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        koAllCell1.setCellValue( krIngredientNames );

        Row enAllRow1 = sheet3.getRow(15);
        if (enAllRow1 == null) enAllRow1 = sheet3.createRow(5);
        Cell enAllCell = enAllRow1.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        enAllCell.setCellValue( enIngredientNames );

        // 3. Response 설정
        String fileName = "recipe_" + map.get("prodName") + ".xlsx";
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
