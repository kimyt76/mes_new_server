package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.proc.service.ProcCommonService;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
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
@RequestMapping("/procCommon")
public class ProcCommonController {

    private final ProcCommonService procCommonService;
    private final MessageUtil messageUtil;

    @GetMapping("/getWorkerList/{id}")
    public List<ProcCommonVo> getWorkerList(@PathVariable("id") String procCd) {
        return  procCommonService.getWorkerList(procCd);

    }

    @GetMapping("/getEquipmentList/{id}")
    public List<ProcCommonVo> getEquipmentList(@PathVariable("id") String storageCd) {
        return  procCommonService.getEquipmentList(storageCd);
    }

    @PostMapping("/updateProcStatus")
    public ResponseEntity<ApiResponse<Void>> updateProcStatus(@RequestBody ProcCommonVo vo) {
        String result = procCommonService.updateProcStatus(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }


}
