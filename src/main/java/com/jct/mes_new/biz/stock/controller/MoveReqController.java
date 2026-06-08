package com.jct.mes_new.biz.stock.controller;

import com.jct.mes_new.biz.stock.service.MoveReqService;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moveReq")
public class MoveReqController {
    private final MoveReqService moveReqService;
    private final MessageUtil messageUtil;


}
