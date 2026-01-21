package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.proc.service.ProcWeighService;
import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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



}
