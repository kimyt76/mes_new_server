package com.jct.mes_new.biz.mat.controller;


import com.jct.mes_new.biz.mat.service.MatPlanService;
import com.jct.mes_new.biz.mat.vo.MatPlanVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mat")
public class MatPlanController {

    private final MatPlanService matPlanService;
    private final MessageUtil messageUtil;


    @PostMapping("/getMatPlanList")
    public List<MatPlanVo> getMatPlanList(@RequestBody MatPlanVo vo) {
        return matPlanService.getMatPlanList(vo);
    }

    @GetMapping("/getMatPlanDetailList/{id}")
    public List<MatPlanVo> getMatPlanDetailList(@PathVariable("id") String matPlanId) {
        return matPlanService.getMatPlanDetailList(matPlanId);
    }


    @PostMapping("/saveMatPlanList")
    public ResponseEntity<ApiResponse<Map<String, String>>> saveMatPlanList(@RequestBody List<MatPlanVo> voList) {
        matPlanService.saveMatPlanList(voList);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), null));
    }


}
