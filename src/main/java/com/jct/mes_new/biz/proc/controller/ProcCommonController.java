package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.proc.service.ProcCommonService;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/procCommon")
public class ProcCommonController {

    private final ProcCommonService procCommonService;


    @GetMapping("/getWorkerList/{id}")
    public List<ProcCommonVo> getWorkerList(@PathVariable("id") String procCd) {
        return  procCommonService.getWorkerList(procCd);

    }

    @GetMapping("/getEquipmentList/{id}")
    public List<ProcCommonVo> getEquipmentList(@PathVariable("id") String storageCd) {
        return  procCommonService.getEquipmentList(storageCd);
    }



}
