package com.jct.mes_new.biz.qc.controller;


import com.jct.mes_new.biz.qc.service.QcTestService;
import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/qcTest")
public class QcTestController {

    private final QcTestService qcTestService;
    private final MessageUtil messageUtil;

    /**
     * qc 품질검사 요청
     * @param vo
     * @return
     */
    @PostMapping("/getQcTestList")
    public List<QcTestVo> getQcTestList (@RequestBody QcTestVo vo) {
        return qcTestService.getQcTestList(vo);
    }

    /**
     * qc 품질검사 상세
     * @param qcTestId
     * @return
     */
    @GetMapping("/getQcTestDetailInfo/{id}")
    public QcTestVo getQcTestDetailInfo (@PathVariable("id") Long qcTestId) {
        return qcTestService.getQcTestDetailInfo(qcTestId);
    }

    /**
     * qc 품질검사 상세 및 메소드
     * @param qcTestId
     * @return
     */
    @GetMapping("/getQcTestInfo/{id}")
    public QcTestRequestVo getQcTestInfo (@PathVariable("id") Long qcTestId) {
        return qcTestService.getQcTestInfo(qcTestId);
    }

    /**
     * 품질검사 상세 업데이트 
     * @param vo
     * @return
     */
    @PostMapping("/updateQcTestDetailInfo")
    public ResponseEntity<ApiResponse<Void>> updateQcTestDetailInfo(@RequestBody QcTestVo vo){
        String msg = qcTestService.updateQcTestDetailInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }

    /**
     * 품질검사 상세 및 메소드 업데이트
     * @param vo
     * @return
     */
    @PostMapping("/updateQcTestInfo")
    public ResponseEntity<ApiResponse<Void>> updateQcTestInfo(@RequestBody QcTestRequestVo vo){
        String msg = qcTestService.updateQcTestInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }

    /**
     * 품질검사 재요청 등록
     * @param vo
     * @return
     */
    @PostMapping("/insertRetestInfo")
    public ResponseEntity<ApiResponse<Void>> insertRetestInfo(@RequestBody QcTestVo vo){
        String msg = qcTestService.insertRetestInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    /**
     * 시험일지 PDF 출력
     * @param qcTestIds
     */
    @PostMapping("/getPrintTest")
    public ResponseEntity<byte[]> getPrintTest(@RequestBody List<Long> qcTestIds) throws Exception{
        byte[] pdfBytes = qcTestService.getPrintTest(qcTestIds);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=qc-test.pdf")
                .body(pdfBytes);
    }

    @GetMapping("/certificateDownloadExcel/{qcTestId}")
    public ResponseEntity<byte[]> certificateDownloadExcel(@PathVariable Long qcTestId) throws Exception {
        byte[] fileBytes = qcTestService.certificateDownloadExcel(qcTestId);
        String fileName = "성적서_" + qcTestId + ".xlsx";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(fileName, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(fileBytes);
    }

    @GetMapping("/tesetDownloadExcel/{qcTestId}")
    public ResponseEntity<byte[]> tesetDownloadExcel(@PathVariable Long qcTestId) throws Exception {
        byte[] fileBytes = qcTestService.tesetDownloadExcel(qcTestId);
        String fileName = "시험일지_" + qcTestId + ".xlsx";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(fileName, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(fileBytes);
    }

}

