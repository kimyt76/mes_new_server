package com.jct.mes_new.biz.qc.controller;


import com.jct.mes_new.biz.qc.service.QcTestService;
import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/qcTest")
public class QcTestController {

    private final QcTestService qcTestService;
    private final MessageUtil messageUtil;

    @PostMapping("/getQcTestList")
    public List<QcTestVo> getQcTestList (@RequestBody QcTestVo vo) {
        return qcTestService.getQcTestList(vo);
    }

    @GetMapping("/getQcTestDetailInfo/{id}")
    public QcTestVo getQcTestDetailInfo (@PathVariable("id") Long qcTestId) {
        return qcTestService.getQcTestDetailInfo(qcTestId);
    }

    @GetMapping("/getQcTestInfo/{id}")
    public QcTestRequestVo getQcTestInfo (@PathVariable("id") Long qcTestId) {
        return qcTestService.getQcTestInfo(qcTestId);
    }

    @PostMapping("/updateQcTestDetailInfo")
    public ResponseEntity<ApiResponse<Void>> updateQcTestDetailInfo(@RequestBody QcTestVo vo){
        String msg = qcTestService.updateQcTestDetailInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }

    @PostMapping("/updateQcTestInfo")
    public ResponseEntity<ApiResponse<Void>> updateQcTestInfo(@RequestBody QcTestRequestVo vo){
        String msg = qcTestService.updateQcTestInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }

    @PostMapping("/insertRetestInfo")
    public ResponseEntity<ApiResponse<Void>> insertRetestInfo(@RequestBody QcTestVo vo){
        String msg = qcTestService.insertRetestInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }


}

