package com.jct.mes_new.biz.common.controller;

import com.jct.mes_new.biz.common.service.CommonService;
import com.jct.mes_new.biz.common.vo.CommonVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {

    private final CommonService commonService;

    @PostMapping("/getCommonList")
    public List<CommonVo> getCommonList(@RequestBody CommonVo commonVo) {
        return commonService.getCommonList(commonVo);
    }

    @GetMapping("/getCodeList/{type}")
    public List<CommonVo> getCodeList(@PathVariable String type ){
        return commonService.getCodeList(type);
    }


}
