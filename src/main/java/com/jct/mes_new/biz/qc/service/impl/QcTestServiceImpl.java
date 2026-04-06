package com.jct.mes_new.biz.qc.service.impl;

import com.jct.mes_new.biz.base.mapper.ItemMapper;

import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.common.mapper.CommonMapper;
import com.jct.mes_new.biz.common.vo.CommonVo;
import com.jct.mes_new.biz.qc.mapper.ItemTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcTestTypeMapper;
import com.jct.mes_new.biz.qc.service.QcTestService;
import com.jct.mes_new.biz.qc.constant.PrintDocumentType;
import com.jct.mes_new.biz.qc.vo.*;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.CodeUtil;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class QcTestServiceImpl implements QcTestService {

    private final QcTestMapper qcTestMapper;
    private final QcTestTypeMapper qcTestTypeMapper;
    private final ItemTestMapper itemTestMapper;
    private final ItemMapper itemMapper;
    private final CommonMapper commonMapper;

    /** 검사 지시 및 성적서 **/
    private final int REPO_MAX_ROW = 15;    // 승인칸 있을 시, 최대 ROW수
    private final int REPO_DIVIDE_CNT = 17; // 승인칸 없을 시, 최대 ROW수
    private final int TEST_REPORT_MAX_ROWS = 15;        // 검사지시 및 성적서 페이지 구분 카운트 (15줄)
    private final int JOURNAL_LINE_CNT = 9;
    /** 시험일지 **/
    private final int TEST_LOG_MAX_ROWS = 10;    // 검사항목 최대 16개 출력

    /**
     * 품질검사요청 조회
     * @param vo
     * @return
     */
    public List<QcTestVo> getQcTestList(QcTestVo vo){
        return qcTestMapper.getQcTestList(vo);
    }

    /**
     * 품질검사 상세 조회
     * @param qcTestId
     * @return
     */
    public QcTestVo getQcTestDetailInfo(Long qcTestId){
        return qcTestMapper.getQcTestDetailInfo(qcTestId);
    }

    /**
     * 품질검사 상세 및 메소드 조회
     * @param qcTestId
     * @return
     */
    public QcTestRequestVo getQcTestInfo(Long qcTestId){
        QcTestRequestVo vo = new QcTestRequestVo();

        vo.setQcTestInfo(this.getQcTestDetailInfo(qcTestId));
        vo.setQcTestTypeMethodList(qcTestTypeMapper.getQcTestTypeMethod(vo.getQcTestInfo().getItemCd()) );
        return vo;
    }

    /**
     * 품질검사 상세 정보 수정
     * @param vo
     * @return
     */
    public String updateQcTestDetailInfo(QcTestVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        if ( qcTestMapper.updateQcTestDetailInfo(vo) <= 0){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        return "수정되었습니다.";
    }

    /**
     * 품질검사 재검사 요청
     * @param vo
     * @return
     */
    public String insertRetestInfo(QcTestVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        if ( qcTestMapper.insertRetestInfo(vo) <= 0){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        return "저장되었습니다.";
    }

    /**
     * 품질검사 등록 및 수정
     * @param vo
     * @return
     */
    public String updateQcTestInfo(QcTestRequestVo vo) {
        String userId = UserUtil.getUserId();

        QcTestVo qcTestMst = vo.getQcTestInfo();
        qcTestMst.setUserId(userId);

        if ( qcTestMapper.updateQcTestAllInfo(qcTestMst) <=  0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //삭제
        List<Long> getDeleteIds = vo.getDeleteIds();
        if (getDeleteIds != null && !getDeleteIds.isEmpty()) {
            qcTestTypeMapper.deleteQcTestTypeMethod(qcTestMst.getItemCd(), vo.getDeleteIds());
        }
        for (QcTestTypeVo item : vo.getQcTestTypeMethodList()) {
            item.setItemCd(qcTestMst.getItemCd());
            item.setUserId(userId);
            item.setQcTestId(qcTestMst.getQcTestId());

            if ( item.getTestTypeMethodId() != null ){
                if ( qcTestTypeMapper.updateTestTypeMethod(qcTestMst.getItemCd(), item) <= 0  ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }else{
                if ( qcTestTypeMapper.insertTestTypeMethod(item) <= 0  ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        //시험내역별 업데이트
        if (itemTestMapper.updateState(qcTestMst) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        return "저장되었습니다.";
    }





    /**
     * 구매 등록시 자동 등록
     * @param qcTestList
     * @param tranId
     * @param userId
     * @return
     */
    public int insertQcTest(List<QcTestVo> qcTestList, Long tranId, String userId){
        int cnt = qcTestMapper.insertQcTest(qcTestList, userId);

        if( cnt <= 0){
            throw new BusinessException(ErrorCode.CREATED);
        }
        return cnt;
    }

    /**
     * 구매등록시 품질검사 등록 수정
     * @param qcTestList
     * @param userId
     * @return
     */
    public int updateQcTest(List<QcTestVo> qcTestList,  String userId){
        for (QcTestVo item : qcTestList ){
            int testNoCnt = qcTestMapper.getTestNoCnt(item.getTestNo(), item.getReqDate());

            if(testNoCnt > 0){
                if (qcTestMapper.updateQcTestInfo(item) <=0){
                    throw new BusinessException(ErrorCode.UPDATED);
                };
            }else{
                if (qcTestMapper.insertSingleQcTest(item ,userId) <=0){
                    throw new BusinessException(ErrorCode.CREATED);
                };
            }
        }
        return 1;
    }

    public byte[] getPrintCertificate(List<Long> qcTestIds) throws Exception {
        return getPrintPdf(qcTestIds, PrintDocumentType.REPORT);
    }

    public byte[] getPrintTest(List<Long> qcTestIds) throws Exception {
        return getPrintPdf(qcTestIds, PrintDocumentType.LOG);
    }

    public byte[] getPrintAll(List<Long> qcTestIds) throws Exception {
        return getPrintPdf(qcTestIds, PrintDocumentType.ALL);
    }


    /**
     * PDF 공통 출력
     */
    public byte[] getPrintPdf(List<Long> qcTestIds, PrintDocumentType printType) throws Exception {
        if (qcTestIds == null || qcTestIds.isEmpty()) {
            throw new BusinessException(getEmptyMessage(printType));
        }
        List<JasperPrint> jasperPrintList = new ArrayList<>();

        for (Long qcTestId : qcTestIds) {
            QcTestRequestVo vo = this.getQcTestInfo(qcTestId);
            if (vo == null) {
                continue;
            }

            QcTestVo mst = vo.getQcTestInfo();
            List<QcTestTypeVo> typeList = vo.getQcTestTypeMethodList();

            if (mst == null) {
                continue;
            }

            String itemGb = CodeUtil.convertItemTypeCd(mst.getItemTypeCd());
            Map<String, Object> param = this.getParamMap(mst, itemGb);

            List<QcTestTypeReportVo> qtItemList = getQtItemList(typeList, mst.getConfirmTesterId());

            // 성적서
            if (printType == PrintDocumentType.REPORT || printType == PrintDocumentType.ALL) {
                List<QcTestTypeReportVo> qtReportList =
                        filledListItem(qtItemList, REPO_MAX_ROW, REPO_DIVIDE_CNT);

                JasperPrint jasperPrintReport = writeJasperReportPage(
                        "quality_test_report_M" + itemGb,
                        qtReportList,
                        param
                );

                if (jasperPrintReport != null) {
                    jasperPrintList.add(jasperPrintReport);
                }
            }

            // 시험일지
            if (printType == PrintDocumentType.LOG || printType == PrintDocumentType.ALL) {
                List<QcTestTypeReportVo> qtLogList =
                        filledListItem(qtItemList, TEST_LOG_MAX_ROWS, TEST_LOG_MAX_ROWS);

                JasperPrint jasperPrintLog = writeJasperReportPage(
                        "quality_test_log_M" + itemGb,
                        qtLogList,
                        param
                );

                if (jasperPrintLog != null) {
                    jasperPrintList.add(jasperPrintLog);
                }
            }
        }

        if (jasperPrintList.isEmpty()) {
            throw new BusinessException("출력할 PDF 데이터가 없습니다.");
        }
        return exportPdf(jasperPrintList);
    }

    private String getEmptyMessage(PrintDocumentType printType) {
        if (printType == PrintDocumentType.REPORT) {
            return "출력할 성적서가 없습니다.";
        } else if (printType == PrintDocumentType.LOG) {
            return "출력할 시험일지가 없습니다.";
        } else {
            return "출력할 PDF 데이터가 없습니다.";
        }
    }

    private byte[] exportPdf(List<JasperPrint> jasperPrintList) throws Exception {
        JRPdfExporter exporter = new JRPdfExporter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();

        return baos.toByteArray();
    }








    /**
     * pdf print
     * @param
     * @return

    public byte[] getPrintTest(List<Long> qcTestIds)  throws Exception {
        if (qcTestIds == null || qcTestIds.isEmpty()) {
            throw new BusinessException("출력할 시험일지가 없습니다.");
        }
        List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
        // 1. qcTestIds로 데이터 조회
        for (Long qcTestId : qcTestIds) {
            QcTestRequestVo vo = this.getQcTestInfo(qcTestId);
            QcTestVo mst = vo.getQcTestInfo();
            List<QcTestTypeVo> typeList = vo.getQcTestTypeMethodList();

            if (mst == null) {
                continue;
            }
            String itemGb = CodeUtil.convertItemTypeCd(mst.getItemTypeCd());
            Map<String, Object> param = this.getParamMap(mst, itemGb);
            // 리스트 구성
            List<QcTestTypeReportVo> qtItemList = getQtItemList(typeList, mst.getConfirmTesterId());
            List<QcTestTypeReportVo> qtReportList = filledListItem(qtItemList, REPO_MAX_ROW, REPO_DIVIDE_CNT);
            List<QcTestTypeReportVo> qtLogList = filledListItem(qtItemList, TEST_LOG_MAX_ROWS, TEST_LOG_MAX_ROWS);
            // 성적서 생성
            JasperPrint jasperPrint1 = writeJasperReportPage("quality_test_report_M" + itemGb, qtReportList, param);
            // 시험일지 생성
            JasperPrint jasperPrint2 = writeJasperReportPage("quality_test_log_M" + itemGb, qtLogList, param);

            if (jasperPrint1 != null) {
                jasperPrintList.add(jasperPrint1);
            }
            if (jasperPrint2 != null) {
                jasperPrintList.add(jasperPrint2);
            }
        }

        if (jasperPrintList.isEmpty()) {
            throw new BusinessException("출력할 PDF 데이터가 없습니다.");
        }

        JRPdfExporter exporter = new JRPdfExporter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();

        return baos.toByteArray();
    }
     */


    public List<QcTestTypeReportVo> filledListItem (List<QcTestTypeReportVo> listItem, int maxRow, int divideCnt) {
        List<QcTestTypeReportVo> resultList = new ArrayList<>(listItem);

        int rowCount = resultList.size();
        int appendRowCount = 0;
        int remainCount = rowCount % ( divideCnt);

        if (remainCount == 0) {
            appendRowCount = maxRow;
        } else if (remainCount > maxRow){
            appendRowCount = divideCnt - remainCount + maxRow;
        } else {
            appendRowCount = maxRow - remainCount;
        }

        while(appendRowCount > 0 ) {
            appendRowCount--;
            QcTestTypeReportVo qtItem = new QcTestTypeReportVo();
            resultList.add(qtItem);
        }
        return resultList;
    }
    //시험 성적서 & 시험일지 레포트 생성.
    public JasperPrint writeJasperReportPage (String templateName, Collection<?> itemList, Map<String, Object> param) throws Exception {
        try {
            InputStream reportStream = getClass().getResourceAsStream("/report/" + templateName + ".jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itemList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, dataSource);
            return jasperPrint;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException( templateName + "생성 중 에러 발생");
        }
    }

    public List<QcTestTypeReportVo> getQtItemList (List<QcTestTypeVo> qtMethods, String confirmMemberCd) {
        List<QcTestTypeReportVo> resultList = new ArrayList<>();

        int rowCount = 0;
        for (QcTestTypeVo qtMethod: qtMethods) {
            rowCount++;
            QcTestTypeReportVo qtItem = new QcTestTypeReportVo();
            qtItem.setRowNo(rowCount);
            qtItem.setTestItem(qtMethod.getTestItem());
            qtItem.setTestMethod(qtMethod.getTestMethod());
            qtItem.setTestSpec(qtMethod.getTestSpec());
            qtItem.setTestResult(qtMethod.getTestResult());
            qtItem.setTestMember(qtMethod.getTesterName());
            qtItem.setTestDateString(qtMethod.getTestDateString());
            qtItem.setPassStateName(qtMethod.getPassState());
            qtItem.setConfirmMember(confirmMemberCd);

            resultList.add(qtItem);
        }
        return resultList;
    }

    public Map<String, Object> getParamMap (QcTestVo qualityTest, String itemGb) throws Exception{
        //품목정보 조회
        String itemCd = qualityTest.getItemCd();
        ItemVo itemMasterView = itemMapper.getItemInfo(itemCd);

        Map<String, Object> resultMap = new HashMap<>();
        //로고 삽입
        BufferedImage logoImage = ImageIO.read(getClass().getResource("/static/images/logo1.png"));
        resultMap.put("logo", logoImage);
        // 반제품(3) 일 경우 제조번호로 표시
        boolean isMat = (itemGb.equals("1") || itemGb.equals("2") || itemGb.equals("6"));
        resultMap.put("titleLotNo", (isMat)? "Lot NO." : "제조번호" );

        //로트번호 상 !!가 있을 경우 띄어쓰기로 변경
        String lotNo = ( qualityTest.getLotNo() == null ) ? "" : qualityTest.getLotNo().replaceAll("!!", " ");
        resultMap.put("printLotNo", (isMat)? lotNo : qualityTest.getMakeNo());
        // 원/부자재 일 경우 구매부, 그 외 생산부
        resultMap.put("reqDept", (itemGb.equals("1") || itemGb.equals("2"))? "구매부" : "생산부");

        // 시험번호 처리, 재검사 일경우 처리 포함.
        String printTestNo = qualityTest.getTestNo();
        if (StringUtils.isNotBlank(qualityTest.getRetestYn()) && qualityTest.getRetestYn().equals("Y")) {
            printTestNo = qualityTest.getTestNo() + " - " + qualityTest.getSeq();
        }
        resultMap.put("printTestNo", printTestNo);

        resultMap.put("itemCd", itemCd);
        resultMap.put("itemName", qualityTest.getItemName());
        resultMap.put("lotNo", lotNo);
        resultMap.put("prodNo", qualityTest.getMakeNo());

        //날짜 처리.
        if (qualityTest.getReqDate() != null) {
            String reqDate = qualityTest.getReqDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            resultMap.put("reqDate", reqDate);
        }
        if (qualityTest.getTestDate() != null) {
            String testDate = qualityTest.getTestDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            resultMap.put("testDate", testDate);
        }
        if (qualityTest.getConfirmDate() != null) {
            String confirmDate = qualityTest.getConfirmDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            resultMap.put("confirmDate", confirmDate);
        }
        resultMap.put("customerName", qualityTest.getCustomerName());
        resultMap.put("reqMember", qualityTest.getReqTesterId());
        resultMap.put("testMember", qualityTest.getTesterId());
        resultMap.put("orderMember", qualityTest.getOrderTesterId());
        resultMap.put("sampleMember", qualityTest.getSampleTesterId());
        resultMap.put("confirmMember", qualityTest.getConfirmTesterId());

        DecimalFormat df1 = new DecimalFormat("#,##0.000");
        DecimalFormat df2 = new DecimalFormat("#,##0");

        String orgUnit = itemMasterView.getUnit();
        //단위가 등록안되어있을경우 기본값은 "kg(ea)"
        String unit = (orgUnit == null || orgUnit.equals(""))? "kg(ea)" : orgUnit;
        String sampleUnit = (unit.equals("kg"))? "g(ml)" : unit;

        String reqQty = (unit.equals("kg"))? df1.format(qualityTest.getReqQty()) : df2.format(qualityTest.getReqQty());

        Double sampleQtyVal = qualityTest.getSampleQty() == null
                ? 0.0
                : Double.valueOf(qualityTest.getSampleQty().toString());
        resultMap.put("reqQty", reqQty + " " + unit);
        resultMap.put("sampleQty", df2.format(sampleQtyVal) + " " + sampleUnit);
        resultMap.put("itemGrp1Name", itemMasterView.getItemGrp1Name());
        resultMap.put("itemGrp3Name", itemMasterView.getItemGrp3Name());

        return resultMap;
    }

    /**
     *  성적서 다운로드
     * @param qcTestId
     * @return
     */
    public byte[] certificateDownloadExcel(Long qcTestId){
        QcTestRequestVo vo = this.getQcTestInfo(qcTestId);
        QcTestVo mst = vo.getQcTestInfo();
        List<QcTestTypeVo> typeList = vo.getQcTestTypeMethodList();

        String itemGb = CodeUtil.convertItemTypeCd(mst.getItemTypeCd());

        List<CommonVo> commonCodeList = commonMapper.getCodeList(CodeUtil.getDocNo(itemGb, "A"));

        int size = typeList.size();
        String templateName = CodeUtil.getTemplateName(itemGb, size);

        try (
                InputStream excelStream =
                        getClass().getResourceAsStream("/excel/" + templateName + ".xlsx");
                Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream)
        ) {

            Sheet sheet = workbook.getSheet("Sheet1");
            Footer footer = sheet.getFooter();

            /** 공통코드 처리 **/
            for (CommonVo item : commonCodeList) {
                if ("DOC_NO".equals(item.getCode())) {
                    footer.setLeft(item.getCodeNm());
                } else {
                    ExcelStyleUtil.getCellRef(sheet, item.getCode())
                            .setCellValue(item.getCodeNm());
                }
            }
            /** 상단 정보 **/
            Map<String, Object> cellValueList = getCellValueList(itemGb, mst);

            for (String key : cellValueList.keySet()) {
                Object value = cellValueList.get(key);

                if (value instanceof Number) {
                    ExcelStyleUtil.getCellRef(sheet, key)
                            .setCellValue(((Number) value).doubleValue());
                } else {
                    ExcelStyleUtil.getCellRef(sheet, key)
                            .setCellValue(value == null ? "" : value.toString());
                }
            }
            /** 검사내역 **/
            int rowNo = (itemGb.equals("3")) ? 14 : 15;

            for (QcTestTypeVo item : typeList) {
                ExcelStyleUtil.getCellRef(sheet, "A" + rowNo).setCellValue(item.getTestItem());
                ExcelStyleUtil.getCellRef(sheet, "D" + rowNo).setCellValue(item.getTestSpec());
                ExcelStyleUtil.getCellRef(sheet, "J" + rowNo).setCellValue(item.getTestResult());
                ExcelStyleUtil.getCellRef(sheet, "P" + rowNo).setCellValue(item.getTestDateString());
                ExcelStyleUtil.getCellRef(sheet, "T" + rowNo).setCellValue(item.getTesterName());
                ExcelStyleUtil.getCellRef(sheet, "V" + rowNo).setCellValue(mst.getConfirmTesterName());
                ExcelStyleUtil.getCellRef(sheet, "X" + rowNo).setCellValue(item.getPassState());
                rowNo++;
            }

            /** 판정일자 **/
            String lastword = templateName.substring(templateName.length() - 1);
            int confirmDateIdx = lastword.equals("1") ? 34 : 52;

            String confirmDate = mst.getConfirmDate()
                    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            ExcelStyleUtil.getCellRef(sheet, "M" + confirmDateIdx).setCellValue(confirmDate);
            ExcelStyleUtil.getCellRef(sheet, "R" + confirmDateIdx).setCellValue(confirmDate);
            ExcelStyleUtil.getCellRef(sheet, "W" + confirmDateIdx).setCellValue(confirmDate);

            /** byte[] 변환 */
            return ExcelStyleUtil.toByteArray(workbook);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("엑셀파일 생성중 에러발생!");
        }
    }

    public Map<String, Object> getCellValueList (String itemGb, QcTestVo qualityTestView) {
        Map<String, Object> result = new HashMap<>();

        //기본 데이터 정리
        String itemName = (qualityTestView.getItemName() != null)? qualityTestView.getItemName() : "";
        String lotNo = (qualityTestView.getLotNo() != null)?   qualityTestView.getLotNo() : "";
        if(!lotNo.equals("")) lotNo.replaceAll("!!", " ");
        String createDate = (qualityTestView.getCreateDate() != null)? qualityTestView.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) : "";
        String dept = (itemGb.equals("1") || itemGb.equals("2"))? "구매부" : "생산부";
        String orderMember = (qualityTestView.getOrderTesterId() != null)? qualityTestView.getOrderTesterId() : "";
        String reqDate = (qualityTestView.getReqDate() != null)? qualityTestView.getReqDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) : "";
        double reqQty = (qualityTestView.getReqQty() != null)? qualityTestView.getReqQty().doubleValue() : 0;
        String reqMember = (qualityTestView.getReqTesterId() != null)? qualityTestView.getReqTesterId() : "";
        String testMember = (qualityTestView.getTesterId() != null)? qualityTestView.getTesterId() : "";
        String sampleMember = (qualityTestView.getSampleTesterId() != null)? qualityTestView.getSampleTesterId() : "";
        String itemCd = (qualityTestView.getItemCd() != null)? qualityTestView.getItemCd(): "";
        String customer = (qualityTestView.getCustomerName() != null)? qualityTestView.getCustomerName() : "";
        String testNo = (qualityTestView.getTestNo() != null)? qualityTestView.getTestNo() : "";
        if (StringUtils.isNotBlank(qualityTestView.getRetestYn()) && qualityTestView.getRetestYn().equals("Y")) {
            testNo = testNo + " - " + qualityTestView.getSeq();
        }
        double sampleQty = (qualityTestView.getSampleQty() != null)? qualityTestView.getSampleQty().doubleValue() : 0;
        String prodNo = (qualityTestView.getMakeNo() != null)? qualityTestView.getMakeNo() : "";
        String storageName = (qualityTestView.getStorageName() != null)? qualityTestView.getStorageName() : "자재 대기장소";

        if(itemGb.equals("1")){
            result.put("E7", itemName);
            result.put("E8", lotNo);
            result.put("E9", createDate);
            result.put("E10", dept);
            result.put("E11", orderMember);
            result.put("E12", reqDate);
            result.put("M9", reqQty);
            result.put("M10", reqMember);
            result.put("M11", testMember);
            result.put("M12", sampleMember);
            result.put("U9", itemCd);
            result.put("U10", customer);
            result.put("U11", testNo);
            result.put("U12", sampleQty);
        }
        if(itemGb.equals("2")) {
            ItemVo itemMasterView = itemMapper.getItemInfo(qualityTestView.getItemCd());
            String itemType1 = (itemMasterView.getItemGrp3Name() != null) ? itemMasterView.getItemGrp3Name() : "";
            String itemType2 = (itemMasterView.getItemGrp1Name() != null) ? itemMasterView.getItemGrp1Name() : "";
            result.put("E7", itemName);
            result.put("E8", itemCd);
            result.put("E9", createDate);
            result.put("E10", dept);
            result.put("E11", orderMember);
            result.put("E12", reqDate);
            result.put("M8", itemType1);
            result.put("M9", reqQty);
            result.put("M10", reqMember);
            result.put("M11", testMember);
            result.put("M12", sampleMember);
            result.put("U8", testNo);
            result.put("U9", itemType2);
            result.put("U10", customer);
            result.put("U11", storageName);
            result.put("U12", sampleQty);
        }
        if(itemGb.equals("3")) {
            result.put("E7", itemName);
            result.put("E8", createDate);
            result.put("E9", dept);
            result.put("E10", orderMember);
            result.put("E11", reqDate);
            result.put("M8", reqQty);
            result.put("M9", reqMember);
            result.put("M10", testMember);
            result.put("M11", sampleMember);
            result.put("U8", itemCd);
            result.put("U9", prodNo);
            result.put("U10", testNo);
            result.put("U11", sampleQty);
        }
        if(itemGb.equals("6")) {
            result.put("E7", itemName);
            result.put("E8", lotNo);
            result.put("E9", reqDate);
            result.put("E10", dept);
            result.put("E11", orderMember);
            result.put("E12", reqDate);
            result.put("M9", reqQty);
            result.put("M10", reqMember);
            result.put("M11", testMember);
            result.put("M12", sampleMember);
            result.put("U9", itemCd);
            result.put("U10", prodNo);
            result.put("U11", testNo);
            result.put("U12", sampleQty);
        }
        return result;
    }


    /**
     * 시험일지 엑셀다운로드
     * @param qcTestId
     * @return
     */
    public byte[] tesetDownloadExcel(Long qcTestId){
        QcTestRequestVo vo = this.getQcTestInfo(qcTestId);
        QcTestVo mst = vo.getQcTestInfo();
        List<QcTestTypeVo> typeList = vo.getQcTestTypeMethodList();

        String itemGb = CodeUtil.convertItemTypeCd(mst.getItemTypeCd());
        List<CommonVo> commonCodeList = commonMapper.getCodeList(CodeUtil.getDocNo(itemGb, "B"));

        try {
            // 양식 로드
            InputStream excelStream = getClass().getResourceAsStream("/excel/qt_journal_list.xlsx");
            Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream);
            Sheet sheet = workbook.getSheet("Sheet1");
            Footer footer = sheet.getFooter();

            // 공통 항목 처리
            for (CommonVo item : commonCodeList) {
                if ("DOC_NO".equals(item.getCode())) {
                    footer.setLeft(item.getCodeNm());
                } else {
                    ExcelStyleUtil.getCellRef(sheet, item.getCode()).setCellValue(item.getCodeNm());
                }
            }
            // D6 품명
            ExcelStyleUtil.getCellRef(sheet, "D6").setCellValue(mst.getItemName());

            // D8 제조번호 or Lot No.
            ExcelStyleUtil.getCellRef(sheet, "D8").setCellValue(
                    "3".equals(itemGb) ? mst.getMakeNo() : mst.getLotNo()
            );
            // P8 시험번호
            ExcelStyleUtil.getCellRef(sheet, "P8").setCellValue(mst.getTestNo());

            // 기본 변수 선언
            int rowNo = 10;      // POI는 0부터 시작 => 엑셀 11행
            short rowHeight = 1310;

            // 양식파일의 첫 줄(11행)의 스타일을 가져옴
            List<CellStyle> cellStyleList = ExcelStyleUtil.getRowCellStyle(sheet, rowNo, 19);

            // 데이터 출력
            for (QcTestTypeVo item : typeList) {
                // 행 생성/가져오기
                ExcelStyleUtil.getRow(sheet, rowNo).setHeight(rowHeight);
                // 병합 보장
                mergeJournalRow(sheet, rowNo);
                // 시험항목 (A:B)
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 0, cellStyleList.get(0)).setCellValue(nvl(item.getTestItem()));
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 1, cellStyleList.get(1)).setCellValue("");
                // 시험방법 (C:F)
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 2, cellStyleList.get(2)).setCellValue(nvl(item.getTestMethod()));
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 3, cellStyleList.get(3)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 4, cellStyleList.get(4)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 5, cellStyleList.get(5)).setCellValue("");
                // 시험기준 (G:J)
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 6, cellStyleList.get(6)).setCellValue(nvl(item.getTestSpec()));
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 7, cellStyleList.get(7)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 8, cellStyleList.get(8)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 9, cellStyleList.get(9)).setCellValue("");
                // 시험결과 (K:S)
                // 필요하면 item.getTestResult() 넣으면 됨
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 10, cellStyleList.get(10)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 11, cellStyleList.get(11)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 12, cellStyleList.get(12)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 13, cellStyleList.get(13)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 14, cellStyleList.get(14)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 15, cellStyleList.get(15)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 16, cellStyleList.get(16)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 17, cellStyleList.get(17)).setCellValue("");
                ExcelStyleUtil.getStyleCell(sheet, rowNo, 18, cellStyleList.get(18)).setCellValue("");

                rowNo++;
            }
            // 마지막 페이지 라인수 맞추기
            int mod = typeList.size() % JOURNAL_LINE_CNT;
            int size = (mod == 0) ? 0 : JOURNAL_LINE_CNT - mod;

            for (int i = 0; i < size; i++) {
                ExcelStyleUtil.getRow(sheet, rowNo).setHeight(rowHeight);
                // 병합 보장
                mergeJournalRow(sheet, rowNo);

                for (int j = 0; j < 19; j++) {
                    ExcelStyleUtil.getStyleCell(sheet, rowNo, j, cellStyleList.get(j)).setCellValue("");
                }
            }

            byte[] excelContent = ExcelStyleUtil.toByteArray(workbook);
            ByteArrayResource resource = new ByteArrayResource(excelContent);
            HttpHeaders header = ExcelStyleUtil.getHeader("qt_journal", excelContent.length);

            return ExcelStyleUtil.toByteArray(workbook);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("엑셀파일 생성중 에러발생!");
        }
    }

    private void mergeJournalRow(Sheet sheet, int rowNo) {
        addMergedRegionIfNotExists(sheet, new CellRangeAddress(rowNo, rowNo, 0, 1));   // A:B
        addMergedRegionIfNotExists(sheet, new CellRangeAddress(rowNo, rowNo, 2, 5));   // C:F
        addMergedRegionIfNotExists(sheet, new CellRangeAddress(rowNo, rowNo, 6, 9));   // G:J
        addMergedRegionIfNotExists(sheet, new CellRangeAddress(rowNo, rowNo, 10, 18)); // K:S
    }

    /**
     * 동일하거나 겹치는 병합영역이 없을 때만 병합 추가
     */
    private void addMergedRegionIfNotExists(Sheet sheet, CellRangeAddress newRegion) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress existingRegion = sheet.getMergedRegion(i);
            if (existingRegion.intersects(newRegion)) {
                return;
            }
        }
        sheet.addMergedRegion(newRegion);
    }

    private String nvl(String str) {
        return str == null ? "" : str;
    }
}
