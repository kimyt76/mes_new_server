package com.jct.mes_new.biz.qc.controller;


import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.qc.service.QcProcTestService;
import com.jct.mes_new.biz.qc.service.QcTestService;
import com.jct.mes_new.biz.qc.vo.QcProcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcProcTestVo;
import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/qcProcTest")
public class QcProcTestController {

    private final QcProcTestService qcProcTestService;
    private final MessageUtil messageUtil;

    @PostMapping("/getQcProcTestList")
    public List<QcProcTestVo> getQcProcTestList (@RequestBody QcProcTestVo vo) {
        return qcProcTestService.getQcProcTestList(vo);
    }

    @PostMapping("/createQcProcTestInfo")
    public ResponseEntity<ApiResponse<Void>> createQcProcTestInfo(@RequestBody QcProcTestVo vo){
        String msg = qcProcTestService.createQcProcTestInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @PostMapping("/getQcProcTestTabInfo")
    public QcProcTestRequestVo getQcProcTestTabInfo (@RequestBody QcProcTestVo vo) {
        return qcProcTestService.getQcProcTestTabInfo(vo);
    }

    @PostMapping("/saveQcProcTestTabInfo")
    public ResponseEntity<ApiResponse<Void>>  saveQcProcTestTabInfo (@RequestBody QcProcTestRequestVo vo) {
        String msg = qcProcTestService.saveQcProcTestTabInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }
    @PostMapping("/saveQcProcTestLineList")
    public ResponseEntity<ApiResponse<Void>>  saveQcProcTestLineList (@RequestBody QcProcTestRequestVo vo) {
        String msg = qcProcTestService.saveQcProcTestLineList(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @GetMapping("/downloadQcProcTest/{id}")
    public ResponseEntity<Resource> downloadQcProcTest(@PathVariable("id") Long qcProcTestMstId ) throws Exception {
        /*
         * Service에서 Excel byte[] 생성
         */
        byte[] excelData = qcProcTestService.downloadQcProcTest(qcProcTestMstId);
        /*
         * Resource 변환
         */
        ByteArrayResource resource = new ByteArrayResource(excelData);
        /*
         * 파일명
         */
        String fileName ="QC_" + qcProcTestMstId + ".xlsx";
        return ResponseEntity.ok()
                /*
                 * 다운로드 파일명
                 */
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                /*
                 * Excel Content-Type
                 */
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                /*
                 * 파일 크기
                 */
                .contentLength(excelData.length)
                /*
                 * 실제 파일
                 */
                .body(resource);
    }


}

