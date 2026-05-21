package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.service.ProcChargeService;
import com.jct.mes_new.biz.proc.vo.ProcChargeVo;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/procCharge")
public class ProcChargeController {

    private final ProcChargeService procChargeService;
    private final MessageUtil messageUtil;

    @PostMapping("/getChargeList")
    public List<ProcChargeVo> getChargeList (@RequestBody ProcChargeVo vo) {
        return  procChargeService.getChargeList(vo);
    }

    @PostMapping("/startProcCharge")
    public ResponseEntity<ApiResponse<Void>>  startProcCharge(@RequestBody ProcChargeVo vo){
        String result = procChargeService.startProcCharge(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), null));
    }


    @PostMapping("/completeCharge")
    public ResponseEntity<ApiResponse<Void>>  completeCharge(@RequestBody ProcChargeVo vo){
        String result = procChargeService.completeCharge(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), null));
    }





}
