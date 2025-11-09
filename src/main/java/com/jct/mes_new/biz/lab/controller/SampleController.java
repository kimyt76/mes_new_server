package com.jct.mes_new.biz.lab.controller;


import com.jct.mes_new.biz.lab.service.SampleService;
import com.jct.mes_new.biz.lab.vo.SampleVo;
import com.jct.mes_new.config.util.ApiResponse;
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

    @PostMapping("/getSampleList")
    public List<SampleVo> getSampleList (@RequestBody SampleVo sampleVo) {
        return sampleService.getSampleList(sampleVo);
    }

    @GetMapping("/getSampleInfo/{id}")
    public SampleVo getSampleInfo (@PathVariable("id") String sampleId) {
        return sampleService.getSampleInfo(sampleId);
    }

    @PostMapping("/saveSampleInfo")
    public ResponseEntity<?> saveSampleInfo(@RequestBody SampleVo vo ) throws Exception {

        try {
            String result = sampleService.saveSampleInfo(vo);

            return ResponseEntity.ok(ApiResponse.success(result));
            //return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("저장에 실패했습니다.", 400));
        }
    }
    @GetMapping("/getNextProdMgmtNo")
    public String getNextProdMgmtNo() {
        return sampleService.getNextProdMgmtNo();
    }
}
