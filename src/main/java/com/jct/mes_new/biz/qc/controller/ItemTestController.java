package com.jct.mes_new.biz.qc.controller;

import com.jct.mes_new.biz.qc.service.ItemTestService;
import com.jct.mes_new.biz.qc.vo.ItemTestVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/itemTest")
public class ItemTestController {

    private final ItemTestService itemTestService;
    private final MessageUtil messageUtil;


    @PostMapping("/getItemTestNoList")
    public List<ItemTestVo> getItemTestNoList(@RequestBody ItemTestVo vo) {
        return itemTestService.getItemTestNoList(vo);
    }

    @GetMapping("/getItemTestNoInfo/{id}")
    public ItemTestVo getItemTestNoInfo(@PathVariable("id") String testNo) {
        return itemTestService.getItemTestNoInfo(testNo);
    }
    @GetMapping("/getItemTestNoInfoList/{id}")
    public List<ItemTestVo> getItemTestNoInfoList(@PathVariable("id") String testNo) {
        return itemTestService.getItemTestNoInfoList(testNo);
    }


    @PostMapping("/updateItemTestNoInfo")
    public ResponseEntity<ApiResponse<Void>> updateItemTestNoInfo(@RequestBody ItemTestVo vo) {
        String result = itemTestService.updateItemTestNoInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }
}
