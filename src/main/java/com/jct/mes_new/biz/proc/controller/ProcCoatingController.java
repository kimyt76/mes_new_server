package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.proc.service.ProcCoatingService;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcProdInfoVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/procCoating")
public class ProcCoatingController {

    private final ProcCoatingService procCoatingService;
    private final MessageUtil messageUtil;

    @PostMapping("/getCoatingList")
    public List<ProcCoatingVo> getCoatingList (@RequestBody ProcCoatingVo vo) {
        return  procCoatingService.getCoatingList(vo);
    }

    @PostMapping("/getCoatingInfo")
    public ProcProdInfoVo getCoatingInfo (@RequestBody ProcCommonVo vo) {
        return  procCoatingService.getCoatingInfo(vo);
    }

    @PostMapping("/startProcCoating")
    public ResponseEntity<ApiResponse<Void>>  startProcCoating(@RequestBody ProcCommonVo vo){
        String result = procCoatingService.startProcCoating(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), null));
    }


}
