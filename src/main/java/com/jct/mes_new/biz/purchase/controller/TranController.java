package com.jct.mes_new.biz.purchase.controller;


import com.jct.mes_new.biz.purchase.service.TranService;
import com.jct.mes_new.biz.purchase.vo.TranRequestVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/invTran")
public class TranController {

    private final TranService invTranService;
    private final MessageUtil messageUtil;


    @GetMapping("/getTranInfo/{id}")
    public TranRequestVo getTranInfo(@PathVariable("id") Long tranId){
        return invTranService.getTranInfo(tranId);
    }



    /**
     * 자재불출 리스트 조회
     * @param vo
     * @return
     */
    @PostMapping("/getItemOutList")
    public List<TranVo> getItemOutList(@RequestBody TranVo vo){
        return invTranService.getItemOutList(vo);
    }

    /**
     * 자재불출 상세 조회
     * @param tranId
     * @return
     */
    @GetMapping("/getItemOutInfo/{id}")
    public TranRequestVo getItemOutInfo(@PathVariable("id") Long tranId){
        return invTranService.getItemOutInfo(tranId);
    }

    /**
     * 자재불출 등록
     * @param vo
     * @return
     */
    @PostMapping("/saveItemOutInfo")
    public ResponseEntity<ApiResponse<?>> saveItemOutInfo (@RequestBody TranRequestVo vo) {
        String result = invTranService.saveItemOutInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }



}
