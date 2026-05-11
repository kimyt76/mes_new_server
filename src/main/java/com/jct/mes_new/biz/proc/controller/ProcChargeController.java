package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.service.ProcChargeService;
import com.jct.mes_new.biz.proc.vo.ProcChargeVo;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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






}
