package com.jct.mes_new.biz.proc.controller;

import com.jct.mes_new.biz.proc.service.ProcMakeService;
import com.jct.mes_new.biz.proc.vo.MakeInfoVo;
import com.jct.mes_new.biz.proc.vo.ProcMakeVo;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/procMat")
public class ProcMakeController {

    private final ProcMakeService procMakeService;
    private final MessageUtil messageUtil;

    @PostMapping("/getMatList")
    public List<ProcMakeVo> getMatList(@RequestBody ProcMakeVo vo) {
        return procMakeService.getMatList(vo);
    }

    @PostMapping("/getMakeInfo")
    public MakeInfoVo getMakeInfo(@RequestBody ProcMakeVo vo) {
        return procMakeService.getMakeInfo(vo);
    }


}
