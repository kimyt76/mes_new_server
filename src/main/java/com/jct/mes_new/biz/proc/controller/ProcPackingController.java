package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.proc.service.ProcPackingService;
import com.jct.mes_new.biz.proc.vo.ProcChargeVo;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.biz.proc.vo.ProcPackingVo;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/procPacking")
public class ProcPackingController {

    private final ProcPackingService procPackingService;
    private final MessageUtil messageUtil;



    @PostMapping("/startProcPacking")
    public ResponseEntity<ApiResponse<Void>> startProcPacking(@RequestBody ProcPackingVo vo){
        String result = procPackingService.startProcPacking(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), null));
    }

    @PostMapping("/completePacking")
    public ResponseEntity<ApiResponse<Void>>  completePacking(@RequestBody ProcPackingVo vo){
        String result = procPackingService.completePacking(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), null));
    }



}
