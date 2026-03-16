package com.jct.mes_new.biz.purchase.controller;


import com.jct.mes_new.biz.purchase.service.TranService;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/invTran")
public class TranController {

    private final TranService invTranService;
    private final MessageUtil messageUtil;



}
