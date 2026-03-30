package com.jct.mes_new.biz.base.controller;

import com.jct.mes_new.biz.base.service.ClientService;
import com.jct.mes_new.biz.base.vo.*;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;
    private final MessageUtil messageUtil;


    @PostMapping("/getClientList")
    public List<ClientVo> getClientList (@RequestBody ClientVo clientVo) {
        return clientService.getClientList(clientVo);
    }

    @GetMapping("/getClientInfo/{id}")
    public ClientRequestVo getClientInfo(@PathVariable("id") String clientId) {
        return clientService.getClientInfo(clientId);
    }

    @PostMapping("/saveClientInfo")
    public ResponseEntity<ApiResponse<Void>> saveClientInfo(@RequestBody ClientRequestVo vo) throws Exception {
        String result = clientService.saveClientInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @GetMapping("/getBusinessNoChecked/{id}")
    public String getBusinessNoChecked(@PathVariable("id") String businessNo) {
        return clientService.getBusinessNoChecked(businessNo);
    }



}
