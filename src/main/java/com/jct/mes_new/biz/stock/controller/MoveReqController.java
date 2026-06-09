package com.jct.mes_new.biz.stock.controller;

import com.jct.mes_new.biz.stock.service.MoveReqService;
import com.jct.mes_new.biz.stock.vo.MoveReqRequestVo;
import com.jct.mes_new.biz.stock.vo.MoveReqVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moveReq")
public class MoveReqController {
    private final MoveReqService moveReqService;
    private final MessageUtil messageUtil;



    @PostMapping("/saveProcMoveReq")
    public ResponseEntity<ApiResponse<?>> saveProcMoveReq (@RequestBody MoveReqRequestVo vo) {
        String result = moveReqService.saveProcMoveReq(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @PostMapping("/saveMoveReq")
    public ResponseEntity<ApiResponse<?>> saveMoveReq (@RequestBody MoveReqRequestVo vo) {
        String result = moveReqService.saveMoveReq(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }






}
