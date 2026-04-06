package com.jct.mes_new.biz.lab.controller;


import com.jct.mes_new.biz.lab.service.BomService;
import com.jct.mes_new.biz.lab.service.SampleService;
import com.jct.mes_new.biz.lab.vo.SampleVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/sample")
public class SampleController {

    private final SampleService sampleService;
    private final MessageUtil messageUtil;

    @PostMapping("/getSampleList")
    public List<SampleVo> getSampleList (@RequestBody SampleVo sampleVo) {
        return sampleService.getSampleList(sampleVo);
    }

    @GetMapping("/getSampleInfo/{id}")
    public SampleVo getSampleInfo (@PathVariable("id") String sampleId) {
        return sampleService.getSampleInfo(sampleId);
    }

    @PostMapping("/saveSampleInfo")
    public ResponseEntity<ApiResponse<Void>> saveSampleInfo(@RequestBody SampleVo vo ){
        String result = sampleService.saveSampleInfo(vo);
        return  ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }
    @GetMapping("/getNextProdMgmtNo")
    public String getNextProdMgmtNo() {
        return sampleService.getNextProdMgmtNo();
    }
}
