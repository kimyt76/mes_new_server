package com.jct.mes_new.biz.base.controller;

import com.jct.mes_new.biz.base.service.ClientService;
import com.jct.mes_new.biz.base.service.DailyReportService;
import com.jct.mes_new.biz.base.vo.*;
import com.jct.mes_new.biz.order.vo.OrderPlanTypeRequestVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dailyReport")
public class DailyReportController {

    private final DailyReportService dailyReportService;
    private final MessageUtil messageUtil;

    /**************************************** 생산일보 공통 ************************************************/

    @PostMapping("/updateDailyReportEndYn")
    public ResponseEntity<ApiResponse<Long>> updateDailyReportEndYn(@RequestBody DailyReportVo vo) {
        String result = dailyReportService.updateDailyReportEndYn(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }


    @PostMapping("/downloadDailyReport")
    public ResponseEntity<byte[]> downloadDailyReport(@RequestBody DailyReportVo vo){
        byte[] fileBytes = dailyReportService.downloadDailyReport(vo);
        String fileName = "DailyReport.xlsx";
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
    /**************************************** 생산일보 원재료 ************************************************/
    @PostMapping("/getM1DailyReportList")
    public List<DailyReportVo> getM1DailyReportList (@RequestBody DailyReportVo vo) {
        return dailyReportService.getM1DailyReportList(vo);
    }

    @GetMapping("/getM1DailyReportInfo")
    public DailyReportRequestVo getM1DailyReportInfo(@RequestParam(required = false) Long dailyId) {
        return dailyReportService.getM1DailyReportInfo(dailyId);
    }

    @PostMapping("/saveDailyReportM1")
    public ResponseEntity<ApiResponse<Long>> saveDailyReportM1(@RequestBody DailyReportRequestVo vo) {
        String result = dailyReportService.saveDailyReportM1(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }


    /**************************************** 생산일보 부재료 ************************************************/
    @PostMapping("/getM2DailyReportList")
    public List<DailyReportVo> getM2DailyReportList (@RequestBody DailyReportVo vo) {
        return dailyReportService.getM2DailyReportList(vo);
    }

    @GetMapping("/getM2DailyReportInfo")
    public DailyReportRequestVo getM2DailyReportInfo(@RequestParam(required = false) Long dailyId) {
        return dailyReportService.getM2DailyReportInfo(dailyId);
    }

    @PostMapping("/saveDailyReportM2")
    public ResponseEntity<ApiResponse<Long>> saveDailyReportM2(@RequestBody DailyReportRequestVo vo) {
        String result = dailyReportService.saveDailyReportM2(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }


    /**************************************** 생산일보 완제품 ************************************************/
    @PostMapping("/getM0DailyReportList")
    public List<DailyReportVo> getM0DailyReportList (@RequestBody DailyReportVo vo) {
        return dailyReportService.getM0DailyReportList(vo);
    }
    @GetMapping("/getM0DailyReportInfo")
    public DailyReportRequestVo getM0DailyReportInfo(@RequestParam(required = false) Long dailyId) {
        return dailyReportService.getM0DailyReportInfo(dailyId);
    }

    @PostMapping("/saveDailyReportM0")
    public ResponseEntity<ApiResponse<Long>> saveDailyReportM0(@RequestBody DailyReportRequestVo vo) {
        String result = dailyReportService.saveDailyReportM0(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }


    /**************************************** 생산일보 인건비 ************************************************/
    @PostMapping("/getDailyLaborCostList")
    public List<DailyLaborCostVo> getDailyLaborCostList (@RequestBody DailyLaborCostVo vo) {
        return dailyReportService.getDailyLaborCostList(vo);
    }

    @GetMapping("/getLaborCostInfo")
    public LaborCostRequestVo getLaborCostInfo(@RequestParam(required = false) Long dailyId) {
        return dailyReportService.getLaborCostInfo(dailyId);
    }

}
