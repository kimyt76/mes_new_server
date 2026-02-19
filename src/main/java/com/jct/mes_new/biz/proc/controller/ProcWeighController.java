package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.proc.service.ProcWeighService;
import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import com.jct.mes_new.biz.proc.vo.WeighInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/procWeigh")
public class ProcWeighController {

    private final ProcWeighService procWeighService;

    @PostMapping("/getWeighList")
    public List<ProcWeighVo> getWeighList(@RequestBody ProcWeighVo vo) {
        return procWeighService.getWeighList(vo);
    }

    @PostMapping("/getWeighInfo")
    public ProcWeighVo getWeighInfo(@RequestBody Map<String, Object> map){
        String workProcId = (String)map.get("workProcId");
        String itemCd = (String)map.get("itemCd");

        ProcWeighVo vo = procWeighService.getWeighInfo(workProcId, itemCd);
        return vo;
    }



}
