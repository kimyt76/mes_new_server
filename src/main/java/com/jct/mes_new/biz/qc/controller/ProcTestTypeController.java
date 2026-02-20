package com.jct.mes_new.biz.qc.controller;


import com.jct.mes_new.biz.qc.service.ProcTestTypeService;
import com.jct.mes_new.biz.qc.vo.ProcTestTypeMethodVo;
import com.jct.mes_new.biz.qc.vo.ProcTestTypeVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import com.jct.mes_new.config.util.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/qc")
public class ProcTestTypeController {

    private final ProcTestTypeService procTestTypeService;
    private final MessageUtil messageUtil;

    @GetMapping("/getProcTestTypeList")
    public List<ProcTestTypeVo> getProcTestTypeList() {
        return procTestTypeService.getProcTestTypeList();
    }

    @GetMapping("/getProcTestTypeMethod/{id}")
    public List<ProcTestTypeMethodVo> getProcTestTypeMethod(@PathVariable("id") String testType) {
        return procTestTypeService.getProcTestTypeMethod(testType);
    }

    @GetMapping("/getProcTestTypeMethodInfo/{id}")
    public ProcTestTypeMethodVo getProcTestTypeMethodInfo(@PathVariable("id") String procTestTypeMethodId) {
        return procTestTypeService.getProcTestTypeMethodInfo(procTestTypeMethodId);
    }

    @PostMapping("/saveProcTestTypeMethod")
    public ResponseEntity<ApiResponse<Void>> saveProcTestTypeMethod (@RequestBody ProcTestTypeMethodVo vo){
        String result = procTestTypeService.saveProcTestTypeMethod(vo);

        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }


}
