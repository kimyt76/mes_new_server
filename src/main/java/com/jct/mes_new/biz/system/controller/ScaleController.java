package com.jct.mes_new.biz.system.controller;

import com.jct.mes_new.biz.system.service.ScaleService;
import com.jct.mes_new.biz.system.vo.ScaleVo;
import com.jct.mes_new.config.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scale")
public class ScaleController {

    private final ScaleService scaleService;

    @PostMapping("/getScaleList")
    private List<ScaleVo> getScaleList(@RequestBody ScaleVo scaleVo) {
        return scaleService.getScaleList(scaleVo);
    }

    @GetMapping("/getScaleInfo/{id}")
    private ScaleVo getScaleInfo(@PathVariable("id") String scaleId) {
        return scaleService.getScaleInfo(scaleId);
    }

    @PostMapping("/saveScaleInfo")
    public RestResponse<ScaleVo> saveScaleInfo(@RequestBody ScaleVo vo){
        ScaleVo saved = scaleService.saveScaleInfo(vo);
        return RestResponse.ok(saved);
    }


}
